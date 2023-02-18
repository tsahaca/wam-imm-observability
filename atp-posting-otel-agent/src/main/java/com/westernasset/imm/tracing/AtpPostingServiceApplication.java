package com.westernasset.imm.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtpPostingServiceApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(AtpPostingServiceApplication.class);
    app.run();
  }
  /**
  @Bean
  public OpenTelemetry openTelemetry() {
    return GlobalOpenTelemetry.get();
  }
  */
}
