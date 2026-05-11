package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum InquiryCategory {
    UNKNOWN(0, "알 수 없음"),
    GENERAL(1, "일반문의"),
    PRODUCT(2, "상품문의"),
    REFUND(3, "환불문의"),
    PAYMENT(4, "결제문의"),
    RETURN(5, "반품문의");

    private final int code;
    private final String description;

    public static InquiryCategory fromCode(int code) {
        return Arrays.stream(values())
                .filter(category -> category.code == code)
                .findFirst()
                .orElse(UNKNOWN);
    }
}