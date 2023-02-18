FROM amazoncorretto:17-alpine3.15-jdk

RUN mkdir -m777 /etc/app
WORKDIR /etc/app
EXPOSE 9081

ADD target/atp-ui-otel-agent.jar /etc/app
ENTRYPOINT ["java", "-jar", "/etc/app/atp-ui-otel-agent.jar"]
