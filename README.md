# Debezium

Change Data Capture (CDC), streaming changes from database. Point it to a database and get notifications for INSERT, UPDATE, DELETE operations… Kafka connector works with DBMS by detecting changes and  stream them to a topic

#### PRE-REQUISITES:

running Zookeeper instance<br />
```docker run -it --rm --name zookeeper -p 2181:2181 -p 2888:2888 -p 3888:3888 quay.io/debezium/zookeeper:2.3```<br />
running Kafka broker<br />
```docker run -it --rm --name kafka -p 9092:9092 --link zookeeper:zookeeper quay.io/debezium/kafka:2.3```<br />
running mySQL database<br />
```docker run -it --rm --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -e MYSQL_USER=user -e MYSQL_PASSWORD=password quay.io/debezium/example-mysql:2.3```<br />

#### MYSQL DATABASE:
```
USE SCHEMA test;
CREATE TABLE users
(
    id   int auto_increment primary key,
    name varchar(20) null,
    age  int         null
);
```
