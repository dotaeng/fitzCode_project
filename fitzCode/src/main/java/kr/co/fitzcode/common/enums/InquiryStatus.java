package kr.co.fitzcode.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum InquiryStatus {
    PENDING(1, "대기 중"),
    ANSWERED(2, "답변 완료"),
    CLOSED(3, "종료됨");

    private final int code;
    private final String description;

    public static InquiryStatus fromCode(int code) {
        return Arrays.stream(InquiryStatus.values())
                .filter(status -> status.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 문의 상태 코드: " + code));
    }
}