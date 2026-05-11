package kr.co.fitzcode.common.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProductStatus {
    ACTIVE(1, "판매중"),
    SOLD_OUT(2, "품절"),
    INACTIVE(3, "비활성화");

    private final int code;
    private final String description;

    ProductStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() { // Thymeleaf에서 호출
        return description;
    }

    public static ProductStatus fromCode(int code) {
        return Arrays.stream(ProductStatus.values())
                .filter(status -> status.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 상태 code: " + code));
    }
}