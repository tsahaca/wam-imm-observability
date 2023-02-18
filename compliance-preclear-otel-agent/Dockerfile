FROM amazoncorretto:17-alpine3.15-jdk

RUN mkdir -m777 /etc/app
COPY postgre.jks /tmp/postgre.jks
WORKDIR /etc/app
EXPOSE 8081

ADD target/compliance-preclear-otel-agent.jar /etc/app
ENTRYPOINT ["java", "-jar", "/etc/app/compliance-preclear-otel-agent.jar"]
