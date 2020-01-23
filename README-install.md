# xmanager-server install
xmanager-server installation procedure

## Fresh install
- create local server configuration to override default settings:
```
cd xmanager-server
cp src/main/resources/application.properties ./custom.properties
```

- create database from [src/test/resources/schema.sql]

- build and run:
```
mvn clean install -Dmaven.test.skip=true
java -jar target/xmanager-server-version.jar --spring.config.location=./custom.properties [--debugserver]
```
