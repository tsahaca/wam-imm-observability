# WAM IMM DISTRIBUTED TRACING DEMO

This is demo project setup with Infrastructure and Applications 
1. To demo how to instrument Flink Application (flink-pipeline-service) manually with OTEL for distributed tracing
2. apt-ui-otel-agent is a plain Spring Boot Application auto instrumented with opentelemetry-javaagent.jar
3. apt-posting-otel-agent is a plain Spring Boot Application auto instrumented with opentelemetry-javaagent.jar

## Infrastructure
1. kafka
2. Flink Cluster
3. Jaeger - Tracing Backend and UI
4. OpenTelemetry Collector

## Use Case Workflow
1. Trader submits a trade using Swagger UI of apt-ui-otel-agent
2. apt-posting-otel-agent saves the trade in Postgres database and publishes to kafka topic orders-topic
3. flink-pipeline-service has 4 components (Source, FilterFunction, MapFunction, Sink)
4. flink-pipeline-service consumes trades from orders-topic, filters the trades with ticketType=FIXED, adds some status and publish it back to trade-output topic


## Prerequisites to build and run
1. Docker Daemon 
2. JDK 11
3. Maven 3+
4. IntelliJ IDEA (optional)

## Build 
1. Go to each project folder (apt-ui-otel-agent, apt-posting-otel-agent, flink-pipeline-service)
2. mvn clean package

## Bring up everything together
1. cd to the project root folder (wam-imm-observability)
2. docker-compose up --build --remove-orphans
3. wait for all the containers to start

## How to access ecah service 
1. Jaeger UI  ( http://localhost:16686 )
2. Kafka Broker (localhost:19092 )
3. atp-ui-otel-agent (http://localhost:9080/swagger-ui/index.html)
4. Apache Flink Dashboard ( http://localhost:9081/#/overview )

## Test
1. Deploy the flink-pipeline-manual.jar from the traget folder of flink-pipeline-service with parrallelism=1 
2. Post a trade using the swagger UI http://localhost:9080/swagger-ui/index.html#/trade/createTrade  ( do not enter tradeId)
3. Sample payload 
4. {
   "tradeId": 0,
   "ticketType": "FIXED",
   "assetId": "CBTYL100",
   "pfNumber": "104",
   "parAmount": 1000
   }


