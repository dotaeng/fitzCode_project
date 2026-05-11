package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {
    CARD(1, "신용/체크카드"),
    TRANSFER(2, "계좌이체"),
    EASY_PAY(3, "간편결제");

    private final int code;
    private final String description;

    PaymentMethod(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentMethod fromCode(int code) {
        for (PaymentMethod method : values()) {
            if (method.code == code) {
                return method;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentMethod code: " + code);
    }
}