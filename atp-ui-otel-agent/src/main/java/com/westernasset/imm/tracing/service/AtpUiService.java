package com.westernasset.imm.tracing.service;

import com.westernasset.imm.tracing.data.TradeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AtpUiService {

    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${services.atp-posting-service.url}")
    private String apiUrl;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public TradeVO saveTrade(TradeVO tradeVO) throws Exception {
        log.info("saveTrade: apiUrl: {} tradeVO: {}", apiUrl, tradeVO);
        ResponseEntity<TradeVO> responseEntity =
                restTemplate(restTemplateBuilder).postForEntity(apiUrl, tradeVO, TradeVO.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        }
        throw new ResponseStatusException(responseEntity.getStatusCode(), "Trade creation failed");
    }

    public TradeVO getTrade(String tradeId) {
        log.info("getTrade: apiUrl: {} ", apiUrl);
        ResponseEntity<TradeVO> responseEntity =
                restTemplate(restTemplateBuilder).getForEntity(apiUrl + "/" + tradeId, TradeVO.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        }
        throw new ResponseStatusException(responseEntity.getStatusCode(), "Trade retrieval failed");
    }

    public List<TradeVO> getTrades() {
        log.info("getTrades: apiUrl: {} ", apiUrl);
        ResponseEntity<List> responseEntity =
                restTemplate(restTemplateBuilder).getForEntity(apiUrl, List.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return responseEntity.getBody();
        }
        throw new ResponseStatusException(responseEntity.getStatusCode(), "Customer retrieval failed");
    }

    public void updateTrade(long tradeId, TradeVO tradeVO) throws Exception {
        log.info("updateTrade: apiUrl: {} tradeVO {} ", apiUrl, tradeVO);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<TradeVO> request = new HttpEntity<>(tradeVO, headers);
        restTemplate(restTemplateBuilder)
                .exchange(apiUrl + "/" + tradeId, HttpMethod.PUT, request, Void.class);
    }

    public void deleteTrade(long tradeId) throws Exception {
        log.info("deleteTrade: apiUrl: {} tradeId {} ", apiUrl, tradeId);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Long> request = new HttpEntity<>(tradeId, headers);
        restTemplate(restTemplateBuilder)
                .exchange(apiUrl + "/" + tradeId, HttpMethod.DELETE, request, Void.class);
    }
}
