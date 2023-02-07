package com.westernasset.imm.tracing.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "global.datasource.truststore")
public class DataSourceTruststoreProperties {
    private String location;
    private String password;
    private String content;
    private String type;
}
