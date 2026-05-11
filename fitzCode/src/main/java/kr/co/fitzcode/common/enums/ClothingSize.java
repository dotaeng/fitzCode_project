package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ClothingSize {
    XS(1, "XS"),
    S(2, "S"),
    M(3, "M"),
    L(4, "L"),
    XL(5, "XL"),
    XXL(6, "XXL");

    private final int code;
    private final String label;

    public static ClothingSize fromCode(int code) {
        for (ClothingSize size : values()) {
            if (size.getCode() == code) {
                return size;
            }
        }
        throw new IllegalArgumentException("적합하지 않은 의류 코드: " + code);
    }
}