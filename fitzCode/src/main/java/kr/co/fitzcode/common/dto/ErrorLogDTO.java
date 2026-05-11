package kr.co.fitzcode.common.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorLogDTO {
    private Long errorId;
    private Integer errorLevel;
    private String message;
    private String stackTrace;
    private LocalDateTime occurredAt;
}