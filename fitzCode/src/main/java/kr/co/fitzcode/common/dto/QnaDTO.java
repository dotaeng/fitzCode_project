package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Schema(description = "Q&A 정보")
public class QnaDTO {
    @Schema(description = "Q&A ID")
    private Long qnaId;

    @Schema(description = "상품 ID")
    private Long productId;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "질문")
    private String question;

    @Schema(description = "답변")
    private String answer;

    @Schema(description = "상태")
    private Integer status;

    @Schema(description = "생성 날짜")
    private Timestamp createdAt;

    @Schema(description = "답변 날짜")
    private Timestamp answeredAt;
}