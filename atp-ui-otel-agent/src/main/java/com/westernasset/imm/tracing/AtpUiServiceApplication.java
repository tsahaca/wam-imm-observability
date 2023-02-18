package com.westernasset.imm.tracing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AtpUiServiceApplication {

  public static void main(String[] args) throws Exception {
    SpringApplication app = new SpringApplication(AtpUiServiceApplication.class);
    app.run();
  }
}
