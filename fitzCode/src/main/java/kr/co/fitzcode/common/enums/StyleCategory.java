package kr.co.fitzcode.common.enums;


import lombok.Getter;

public enum StyleCategory {
    CASUAL(1),
    STREET(2),
    CHIC(3),
    GORPCORE(4),
    CLASSIC(5);

    @Getter
    private final int styleId;

    StyleCategory(int styleId) {
        this.styleId = styleId;
    }

    public static StyleCategory fromStyleId(int styleId) {
        for (StyleCategory category : values()) {
            if (category.styleId == styleId) {
                return category;
            }
        }
        throw new IllegalArgumentException("올바른 스타일 ID가 아님: " + styleId);
    }
}

