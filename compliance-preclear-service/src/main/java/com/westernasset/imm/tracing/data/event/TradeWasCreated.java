package com.westernasset.imm.tracing.data.event;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.westernasset.imm.tracing.data.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"tradeId", "ticketType", "assetId", "pfNumber", "parAmount", "status"})
public class TradeWasCreated {

   // @Size(max = 36)
    private Long tradeId;

    @NotBlank @Size(max = 50)
    private String ticketType;

    @NotBlank @Size(max = 50)
    private String assetId;

    @NotBlank @Size(max = 50)
    private String pfNumber;

    @NotBlank
    private BigDecimal parAmount;

    private TradeStatus status = TradeStatus.CREATED;

}
