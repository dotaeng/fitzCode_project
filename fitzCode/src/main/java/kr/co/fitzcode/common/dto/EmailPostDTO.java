package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "이메일 요청 정보")
public class EmailPostDTO {
    @Schema(description = "이메일")
    private String email;
}