FROM amazoncorretto:17-alpine3.15-jdk

# Datadog agent installation
#RUN apk --no-cache add curl && curl -s -L -o /dd-java-agent.jar "https://dtdg.co/latest-java-tracer"

RUN mkdir -m777 /etc/atp-ui-service
WORKDIR /etc/atp-ui-service
EXPOSE 8081

ADD target/atp-ui-service-0.0.1-SNAPSHOT.jar /etc/atp-ui-service
ENTRYPOINT ["java", "-jar", "/etc/atp-ui-service/atp-ui-service-0.0.1-SNAPSHOT.jar"]
