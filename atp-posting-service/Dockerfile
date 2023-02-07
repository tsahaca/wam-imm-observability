FROM amazoncorretto:17-alpine3.15-jdk

# Datadog agent installation
#RUN apk --no-cache add curl && curl -s -L -o /dd-java-agent.jar "https://dtdg.co/latest-java-tracer"

RUN mkdir -m777 /etc/atp-posting-service
COPY postgre.jks /tmp/postgre.jks
WORKDIR /etc/atp-posting-service
EXPOSE 8081

ADD target/atp-posting-service-0.0.1-SNAPSHOT.jar /etc/atp-posting-service
ENTRYPOINT ["java", "-jar", "/etc/atp-posting-service/atp-posting-service-0.0.1-SNAPSHOT.jar"]
