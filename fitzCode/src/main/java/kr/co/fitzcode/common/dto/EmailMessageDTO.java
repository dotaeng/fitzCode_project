package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "이메일 메시지 정보")
public class EmailMessageDTO {
    @Schema(description = "수신자")
    private String to;

    @Schema(description = "제목")
    private String subject;

    @Schema(description = "내용")
    private String message;
}