package com.moriset.bcephal.manager.model.enumeration;

import java.util.Objects;

public enum InvoiceType {

    INVOICE("invoice"),
    CREDIT_NOTE("credit note");

    private String value;

    InvoiceType(String value) {
        this.value = value;
    }

    public String getValue() { return value; }

    public void setValue(String value) { this.value = value; }

    public static InvoiceType forValue(String value) {
        for (InvoiceType invoiceType : values()) {
            if (Objects.equals(invoiceType.getValue(), value)) {
                return invoiceType;
            }
        }
        return null;
    }

}
