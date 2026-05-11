package kr.co.fitzcode.common.enums;

import lombok.Getter;

@Getter
public enum CouponUsageStatus {
    UNUSED(0, "미사용"),
    USED(1, "사용됨");

    private final int code;
    private final String description;

    CouponUsageStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static CouponUsageStatus fromCode(int code) {
        for (CouponUsageStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CouponUsageStatus code: " + code);
    }
}