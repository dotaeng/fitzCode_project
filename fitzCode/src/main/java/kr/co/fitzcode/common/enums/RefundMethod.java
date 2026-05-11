package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum RefundMethod {
    CARD(1, "카드 환불"),
    TRANSFER(2, "계좌이체 환불"),
    EASY_PAY(3, "간편결제 환불");

    private final int code;
    private final String description;

    RefundMethod(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RefundMethod fromCode(int code) {
        for (RefundMethod method : values()) {
            if (method.code == code) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid RefundMethod code: " + code);
    }
}