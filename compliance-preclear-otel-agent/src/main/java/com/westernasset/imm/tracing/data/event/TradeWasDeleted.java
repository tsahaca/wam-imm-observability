package com.westernasset.imm.tracing.data.event;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.westernasset.imm.tracing.data.TradeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"tradeId", "status"})
public class TradeWasDeleted {

   // @Size(max = 36)
    private Long tradeId;

    private TradeStatus status = TradeStatus.DELETED;

}
