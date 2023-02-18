package com.westernasset.imm.tracing.data;

public enum TradeStatus {
    CREATED,
    UPDATED,
    DELETED;

    public String value() {
        return name();
    }

    public static TradeStatus fromValue(String v) {
        return valueOf(v);
    }
}
