package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "알림 정보")
public class NotificationDTO {
    @Schema(description = "알림 고유 ID")
    private Integer notificationId;

    @Schema(description = "사용자 ID")
    private Integer userId;

    @Schema(description = "알림 유형")
    private Integer type;

    @Schema(description = "알림 메시지")
    private String message;

    @Schema(description = "읽음 여부")
    private Boolean isRead;

    @Schema(description = "알림 생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "알림 읽은 시간")
    private LocalDateTime readAt;

    @Schema(description = "관련된 공지사항 ID")
    private Integer relatedId;
}