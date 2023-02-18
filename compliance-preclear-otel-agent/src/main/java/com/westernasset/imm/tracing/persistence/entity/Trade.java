package com.westernasset.imm.tracing.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TRADE")
public class Trade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TRADE_ID")
    private long tradeId;

    @Column(name = "TICKET_TYPE")
    private String ticketType;

    @Column(name = "ASSET_ID")
    private String assetId;

    @Column(name = "PF_NUMBER")
    private String pfNumber;

    @Column(name = "PAR_AMOUNT")
    private BigDecimal parAmount;
}
