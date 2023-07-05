package com.example.debezium.listener;

import com.example.debezium.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.Operation;
import static java.util.stream.Collectors.toMap;

@Component
public class DebeziumListener {

    private final Logger log = LoggerFactory.getLogger(DebeziumListener.class);
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final DebeziumEngine<RecordChangeEvent<SourceRecord>> debeziumEngine;

    public DebeziumListener(Configuration customConnector) {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
                .using(customConnector.asProperties()) // from @Bean configuration
                .notifying(this::handleChangeEvent)
                .build();
    }

    private void handleChangeEvent(RecordChangeEvent<SourceRecord> rowChangeEvent) {
        log.info("RowChangeEvent = {}", rowChangeEvent);

        SourceRecord sourceRecord = rowChangeEvent.record();
        Struct sourceRecordChangeValue = (Struct) sourceRecord.value();

        if (sourceRecordChangeValue != null) {
            Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

            if (operation != Operation.READ) {
                String record = operation == Operation.DELETE ? BEFORE : AFTER; // Handling Update & Insert operations.

                Struct struct = (Struct) sourceRecordChangeValue.get(record);
                Map<String, Object> payload = struct.schema().fields().stream()
                        .map(Field::name)
                        .filter(fieldName -> struct.get(fieldName) != null)
                        .map(fieldName -> Pair.of(fieldName, struct.get(fieldName)))
                        .collect(toMap(Pair::getKey, Pair::getValue));

                final ObjectMapper mapper = new ObjectMapper();
                final Customer customer = mapper.convertValue(payload, Customer.class);

                log.info("SQL Operation: {}, Data: {}", operation.name(), customer);
            }
        }
    }

    @PostConstruct
    private void start() {
        this.executor.execute(debeziumEngine);
    }

    @PreDestroy
    private void stop() throws IOException {
        if (this.debeziumEngine != null) {
            this.debeziumEngine.close();
        }
    }

}