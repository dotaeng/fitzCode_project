package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    COMPLETED(1, "결제 완료"),
    FAILED(2, "결제 실패");

    private final int code;
    private final String description;

    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PaymentStatus fromCode(int code) {
        for (PaymentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentStatus code: " + code);
    }
}