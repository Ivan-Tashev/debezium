package com.example.debezium.config;

import io.debezium.config.Configuration;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;

@org.springframework.context.annotation.Configuration
public class DebeziumConfig {

    @Bean
    public Configuration customConnector() throws IOException {
//        File offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
//        File dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");
        return Configuration.create() // io.debezium.config.Configuration
                .with("name", "customer-mysql-connector")
                .with("connector.class", "io.debezium.connector.mysql.MySqlConnector") // track db
                .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                .with("offset.storage.file.filename", "/tmp/offsets.dat")
                .with("offset.flush.interval.ms", "60000")
                .with("topic.prefix", "test_topic")
                .with("schema.history.internal.kafka.topic", "test_topic")
                .with("schema.history.internal.kafka.bootstrap.servers", "localhost:9092")
                .with("database.hostname", "localhost")
                .with("database.port", 3306)
                .with("database.connectionTimeZone", "UTC")
                .with("database.user", "root")
                .with("database.password", "xxxx")
                .with("database.dbname", "test")
                .with("database.include.list", "test")
                .with("include.schema.changes", "false")
                .with("database.server.id", "1")
                .with("database.server.name", "localhost")
                .with("database.history", "io.debezium.relational.history.FileDatabaseHistory") // store offsets to local file
                .with("database.history.file.filename", "/tmp/dbhistory.dat")
                .build();
    }
}
