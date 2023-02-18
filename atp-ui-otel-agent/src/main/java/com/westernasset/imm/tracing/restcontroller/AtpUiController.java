package com.westernasset.imm.tracing.restcontroller;

import com.westernasset.imm.tracing.data.TradeVO;
import com.westernasset.imm.tracing.service.AtpUiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/trades",
        produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "trade", description = "Operations pertaining to atp service")
public class AtpUiController {

    private static final String JSON = MediaType.APPLICATION_JSON_VALUE;

    private final AtpUiService atpUiService;

    @Operation(summary = "Create trade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created a trade"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authorization denied"),
            @ApiResponse(responseCode = "500", description = "Unexpected system exception"),
            @ApiResponse(responseCode = "502", description = "An error has occurred with an upstream service")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createTrade(@Valid @RequestBody TradeVO tradeVO, UriComponentsBuilder uriBuilder)
        throws Exception {
        TradeVO newTradeVO = atpUiService.saveTrade(tradeVO);
        URI location = uriBuilder
                .path("/trades/{tradeId}")
                .buildAndExpand(newTradeVO.getTradeId())
                .toUri();
        return ResponseEntity.created(location)
                .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                .body(TradeVO.builder()
                        .tradeId(newTradeVO.getTradeId())
                        .ticketType(newTradeVO.getTicketType())
                        .assetId(newTradeVO.getAssetId())
                        .pfNumber(newTradeVO.getPfNumber())
                        .parAmount(newTradeVO.getParAmount())
                        .build());
    }

    @Operation(summary = "Retrieve all trades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all customers"),
            @ApiResponse(responseCode = "401", description = "Authorization denied"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Unexpected system exception")
    })
    @GetMapping(produces = JSON)
    public ResponseEntity<List> getTrades() {
        return ResponseEntity.ok(atpUiService.getTrades());
    }

    @Operation(summary = "Retrieve the trade details given the trade Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved a trade"),
            @ApiResponse(responseCode = "401", description = "Authorization denied"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "500", description = "Unexpected system exception")
    })
    @GetMapping(value = "/{tradeId}")
    public ResponseEntity<TradeVO> getTrade(@PathVariable String tradeId) {
        return ResponseEntity.ok(atpUiService.getTrade(tradeId));
    }

    @Operation(summary = "Update Trade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully updated a customer"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authorization denied"),
            @ApiResponse(responseCode = "500", description = "Unexpected system exception"),
            @ApiResponse(responseCode = "502", description = "An error has occurred with an upstream service")
    })
    @PutMapping(value = "/{tradeId}", consumes = JSON)
    public ResponseEntity updateTrade(@PathVariable long tradeId, @RequestBody TradeVO tradeVO)
        throws Exception {
        atpUiService.updateTrade(tradeId, tradeVO);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete Trade")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted a Trade"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
            @ApiResponse(responseCode = "401", description = "Authorization denied"),
            @ApiResponse(responseCode = "500", description = "Unexpected system exception"),
            @ApiResponse(responseCode = "502", description = "An error has occurred with an upstream service")
    })
    @DeleteMapping(value = "/{tradeId}")
    public ResponseEntity deleteTrade(@PathVariable long tradeId) throws Exception {
        atpUiService.deleteTrade(tradeId);
        return ResponseEntity.noContent().build();
    }
}
