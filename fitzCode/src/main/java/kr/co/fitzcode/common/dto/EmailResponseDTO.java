package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이메일 응답 정보")
public class EmailResponseDTO {
    @Schema(description = "코드")
    private String code;
}