FROM amazoncorretto:17-alpine3.15-jdk

# Datadog agent installation
#RUN apk --no-cache add curl && curl -s -L -o /dd-java-agent.jar "https://dtdg.co/latest-java-tracer"

RUN mkdir -m777 /etc/compliance-preclear-service
COPY postgre.jks /tmp/postgre.jks
WORKDIR /etc/compliance-preclear-service
EXPOSE 8081

ADD target/compliance-preclear-service-0.0.1-SNAPSHOT.jar /etc/compliance-preclear-service
ENTRYPOINT ["java", "-jar", "/etc/compliance-preclear-service/compliance-preclear-service-0.0.1-SNAPSHOT.jar"]
