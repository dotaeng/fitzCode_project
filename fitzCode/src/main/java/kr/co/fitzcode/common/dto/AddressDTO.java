package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주소 정보")
public class AddressDTO {
    @Schema(description = "사용자 ID")
    private int userId;

    @Schema(description = "주소 ID")
    private int addressId;

    @Schema(description = "주소1")
    private String addressLine1;

    @Schema(description = "주소2")
    private String addressLine2;

    @Schema(description = "우편번호")
    private String postalCode;

    @Schema(description = "기본 배송지 여부")
    private Boolean isDefault;


    public boolean isDefault() {
        return isDefault;
    }
}