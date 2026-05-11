package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "방문자 정보")
public class VisitorDTO {
    @Schema(description = "방문 로그 ID")
    private Long id;

    @Schema(description = "로그인한 사용자 ID")
    private Long userId;

    @Schema(description = "비회원 UUID")
    private String visitorId;

    @Schema(description = "방문 시간")
    private LocalDateTime visitTime;

    @Schema(description = "방문한 페이지 URL")
    private String pageUrl;

    @Schema(description = "유입 경로")
    private String referrerUrl;

    @Schema(description = "브라우저/OS 정보")
    private String userAgent;

    @Schema(description = "디바이스 타입")
    private Integer deviceType;

    @Schema(description = "IP 주소")
    private String ipAddress;

    @Schema(description = "날짜별 집계용")
    private LocalDate visitDate;

    @Schema(description = "방문자 수")
    private Integer count;
}