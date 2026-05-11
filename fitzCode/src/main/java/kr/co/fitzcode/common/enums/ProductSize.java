package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ProductSize {
    // 신발 사이즈
    SIZE_220(1, "220"), SIZE_225(2, "225"), SIZE_230(3, "230"),
    SIZE_235(4, "235"), SIZE_240(5, "240"), SIZE_245(6, "245"),
    SIZE_250(7, "250"), SIZE_255(8, "255"), SIZE_260(9, "260"),

    // 의류 사이즈
    SIZE_S(10, "S"), SIZE_M(11, "M"), SIZE_L(12, "L"),
    SIZE_XL(13, "XL"), SIZE_XXL(14, "XXL"),

    // 프리 사이즈
    SIZE_FREE(15, "FREE");

    private final int code;
    private final String description;

    /**
     * 주어진 코드값으로 ProductSize Enum을 찾는 메서드
     * @param code DB에서 조회한 size_code 값
     * @return ProductSize Enum 객체
     * @throws IllegalArgumentException 유효하지 않은 코드 값인 경우 예외 발생
     */
    public static ProductSize fromCode(int code) {
        return Arrays.stream(ProductSize.values())
                .filter(size -> size.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사이즈 코드: " + code));
    }
}