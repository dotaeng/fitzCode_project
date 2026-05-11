package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 정보")
public class NoticeDTO {
    @Schema(description = "공지사항 ID")
    private int noticeId;

    @Schema(description = "사용자 ID")
    private int userId;

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String content;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "첨부파일 URL")
    private String attachmentUrl;

    @Schema(description = "첨부파일 이름")
    private String attachmentName;

    @Schema(description = "첨부파일 크기")
    private Long attachmentSize;

    @Schema(description = "생성 날짜")
    private LocalDateTime createdAt;

    @Schema(description = "수정 날짜")
    private LocalDateTime updatedAt;
}