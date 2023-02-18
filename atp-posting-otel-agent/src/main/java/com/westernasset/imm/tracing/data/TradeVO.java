package com.westernasset.imm.tracing.data;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
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
@JsonPropertyOrder({"tradeId", "ticketType", "assetId","pfNumber","parAmount"})
public class TradeVO {

    @Schema(required = false, description = "The ID for the trade. Should be a Unique ID with max 36 Characters. If not provided, the system will assign one.")
    private Long tradeId;

    @Schema(required = true, description = "The Ticket Type. The length cannot exceed 50.")
    @NotBlank @Size(max = 50)
    private String ticketType;

    @Schema(required = true, description = "The Asset ID. The length cannot exceed 50.")
    @NotBlank @Size(max = 50)
    private String assetId;

    @Schema(required = true, description = "Portfolio Number. The length cannot exceed 50.")
    @NotBlank @Size(max = 50)
    private String pfNumber;

    @Schema(required = true, description = "Par Amount. Cannot be Null ")
    @NotBlank
    private BigDecimal parAmount;
}
