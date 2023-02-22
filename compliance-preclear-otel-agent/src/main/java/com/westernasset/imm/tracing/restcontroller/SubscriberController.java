package com.westernasset.imm.tracing.restcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.westernasset.imm.tracing.data.TradeVO;
import com.westernasset.imm.tracing.data.event.TradeWasCreated;
import com.westernasset.imm.tracing.data.event.TradeWasDeleted;
import com.westernasset.imm.tracing.data.event.TradeWasUpdated;
import com.westernasset.imm.tracing.service.TradePreClearService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "subscriber", description = "Operations pertaining to Compliance Preclear Service event consumption")
public class SubscriberController {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TradePreClearService orderService;

    @Operation(summary = "Consume Trade CRUD Event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully consumed Trade CRUD event"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Unexpected system exception"),
            @ApiResponse(responseCode = "502", description = "An error has occurred with an upstream service")
    })

    @KafkaListener(topics = "orders-topic")
    @PostMapping(path = "/order-operations")
    public void consumeCustomerCrudEvent(@RequestBody(required = false) TradeVO event,
                                         @Headers MessageHeaders messageHeaders) {
        log.info("Received Message: " + event);
        log.info("Received MessageHeaders: ");
        messageHeaders.keySet().forEach( key -> {
            Object value = messageHeaders.get(key);
            log.info("header-{}: {}", key, value);
        });
        log.info("End of Received MessageHeaders: ");
        try {
            String stringValue = OBJECT_MAPPER.writeValueAsString(event);
            log.info("received event {}", OBJECT_MAPPER.writeValueAsString(event));
            if (stringValue.contains("CREATED")) {
                orderService.consumeTradeWasCreated(
                        OBJECT_MAPPER.convertValue(event, TradeWasCreated.class));
            } else if (stringValue.contains("UPDATED")) {
                orderService.consumeTradeWasUpdated(
                        OBJECT_MAPPER.convertValue(event, TradeWasUpdated.class));
            } else if (stringValue.contains("DELETED")) {
                orderService.consumeTradeWasDeleted(
                        OBJECT_MAPPER.convertValue(event, TradeWasDeleted.class));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
