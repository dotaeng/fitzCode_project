package kr.co.fitzcode.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.fitzcode.admin.service.LogService;
import kr.co.fitzcode.common.dto.ErrorLogDTO;
import kr.co.fitzcode.common.dto.VisitorDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/admin/logs")
@RequiredArgsConstructor
@Tag(name = "Log API", description = "관리자 로그 조회 API 제공, 에러 로그 및 방문자 로그 표시")
public class AdminLogController {

    private final LogService logService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH:mm");
    private static final DateTimeFormatter INPUT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Operation(summary = "로그 관리 페이지", description = "에러 로그와 방문자 로그를 표시하는 관리자 페이지")
    @GetMapping("/logManagement")
    public String showLogManagement() {
        return "admin/log/logManagement";
    }

    @Operation(summary = "에러 로그 조회", description = "시간 범위와 레벨로 필터링된 에러 로그 조회")
    @GetMapping("/error")
    public ResponseEntity<String> getErrorLog(
            @Parameter(description = "시작 날짜 (형식: yyyy-MM-dd'T'HH:mm)", example = "2025-03-18T00:00")
            @RequestParam(value = "startDate", required = false) String startDate,
            @Parameter(description = "종료 날짜 (형식: yyyy-MM-dd'T'HH:mm)", example = "2025-03-18T23:59")
            @RequestParam(value = "endDate", required = false) String endDate,
            @Parameter(description = "최소 에러 레벨 (1: WARN, 2: ERROR, 3: CRITICAL)", example = "1")
            @RequestParam(value = "minLevel", required = false, defaultValue = "1") int minLevel) {
        LocalDateTime start = (startDate != null && !startDate.isEmpty()) ? LocalDateTime.parse(startDate, INPUT_DATE_FORMATTER) : null;
        LocalDateTime end = (endDate != null && !endDate.isEmpty()) ? LocalDateTime.parse(endDate, INPUT_DATE_FORMATTER) : null;

        List<ErrorLogDTO> errorLogs = logService.getFilteredErrorLogs(start, end, minLevel);
        String logText = errorLogs.stream()
                .map(log -> String.format("발생 시간: %s | 레벨: %d | 메시지: %s | 스택 트레이스: %s",
                        log.getOccurredAt().format(DATE_FORMATTER),
                        log.getErrorLevel(),
                        log.getMessage(),
                        log.getStackTrace() != null ? log.getStackTrace() : "없음"))
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(logText);
    }

    @Operation(summary = "방문자 로그 조회", description = "시간 범위로 필터링된 방문자 로그 조회")
    @GetMapping("/visitor")
    public ResponseEntity<String> getVisitorLog(
            @Parameter(description = "시작 날짜 (형식: yyyy-MM-dd'T'HH:mm)", example = "2025-03-18T00:00")
            @RequestParam(value = "startDate", required = false) String startDate,
            @Parameter(description = "종료 날짜 (형식: yyyy-MM-dd'T'HH:mm)", example = "2025-03-18T23:59")
            @RequestParam(value = "endDate", required = false) String endDate) {
        LocalDateTime start = (startDate != null && !startDate.isEmpty()) ? LocalDateTime.parse(startDate, INPUT_DATE_FORMATTER) : null;
        LocalDateTime end = (endDate != null && !endDate.isEmpty()) ? LocalDateTime.parse(endDate, INPUT_DATE_FORMATTER) : null;

        List<VisitorDTO> visitorLogs = logService.getFilteredVisitorLogs(start, end);
        String logText = visitorLogs.stream()
                .map(log -> String.format("방문 시간: %s | 사용자 ID: %s | 방문자 ID: %s | 유입 경로: %s | 디바이스: %s | IP: %s",
                        log.getVisitTime().format(DATE_FORMATTER),
                        log.getUserId() != null ? log.getUserId() : "-",
                        log.getVisitorId(),
                        log.getReferrerUrl() != null ? log.getReferrerUrl() : "-",
                        log.getDeviceType() != null ? (log.getDeviceType() == 1 ? "모바일" : "PC") : "-",
                        log.getIpAddress() != null ? log.getIpAddress() : "-"))
                .collect(Collectors.joining("\n"));
        return ResponseEntity.ok(logText);
    }
}