package kr.co.fitzcode.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BankIcon {
    WOORI ("우리은행", "/img/wooriicon.jpg"),
    KB ("국민은행", "/img/kbicon.jpg"),
    SH ("신한은행", "/img/shinhanicon.png"),
    NH ("농협은행", "/img/nhicon.svg"),
    TOSS ("토스뱅크", "/img/tossicon.png"),
    KAKAO ("카카오뱅크", "/img/kakaoBankicon.png");

    private final String bankName;
    private final String imgPath;

    public static BankIcon fromBankName(String bankName) {
        return Arrays.stream(BankIcon.values())
                .filter(bankIcon -> bankIcon.getBankName().equals(bankName))
                .findFirst()
                .orElse(BankIcon.WOORI);
    }
}
