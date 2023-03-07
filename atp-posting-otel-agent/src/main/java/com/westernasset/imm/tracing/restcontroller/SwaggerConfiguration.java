package com.westernasset.imm.tracing.restcontroller;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {
    private static final String TITLE = "ATP POsting Otel Agent";
    private static final String DESCRIPTION = "Application used for ATP POsting Otel Agent";
    private static final String VERSION = "1.0";
    private static final String URL = "https://github.com/tsahaca";

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("atp-posting-otel-agent")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(TITLE)
                        .description(DESCRIPTION)
                        .version(VERSION)
                        .license(new License().name("Apache 2.0").url(URL)))
                .externalDocs(new ExternalDocumentation()
                        .description("ATP POsting Otel Agent service Wiki Documentation")
                        .url("https://github.com/tsahaca"));
    }
}
