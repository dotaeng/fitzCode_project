package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum DeviceType {
    MOBILE("MOBILE", "모바일"),
    DESKTOP("DESKTOP", "데스크탑");

    private final String code;        // DB에 저장될 값
    private final String description; // 설명

    public static DeviceType fromCode(String code) {
        return Arrays.stream(DeviceType.values())
                .filter(e -> e.getCode().equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 디바이스 타입: " + code));
    }
}