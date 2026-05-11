package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.fitzcode.common.enums.BankIcon;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "계좌 정보")
public class AccountDTO {
    @Schema(description = "계좌 ID")
    private int accountId;

    @Schema(description = "사용자 ID")
    private int userId;

    @Schema(description = "은행명")
    private String bankName;

    @Schema(description = "계좌번호")
    private String accountNumber;

    @Schema(description = "계좌주")
    private String accountHolder;

    private Boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    public BankIcon getBankIcon() {
        return BankIcon.valueOf(this.bankName);
    }

    public BankIcon getBankIcon(String bankName) {
        return BankIcon.fromBankName(bankName);
    }
}