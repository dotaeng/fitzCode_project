package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum RefundStatus {
    REQUESTED(1, "환불 요청"),
    PROCESSING(2, "환불 진행 중"),
    COMPLETED(3, "환불 완료"),
    REJECTED(4, "환불 거절");

    private final int code;
    private final String description;

    RefundStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RefundStatus fromCode(int code) {
        for (RefundStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid RefundStatus code: " + code);
    }
}