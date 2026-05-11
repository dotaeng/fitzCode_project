package kr.co.fitzcode.common.exception;

import kr.co.fitzcode.admin.exception.NoticeNotFoundException;
import kr.co.fitzcode.common.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception e, Model model, HttpServletRequest request) {
        log.error("예외 발생: URL={}, 메시지={}", request.getRequestURI(), e.getMessage(), e);

        // API 요청인지 확인
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Internal Server Error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // HTML 요청 처리
        model.addAttribute("error", "error");
        return "404-error";
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public Object handleBadSqlGrammarException(BadSqlGrammarException ex, Model model, HttpServletRequest request) {
        log.error("SQL 문법 오류 발생: URL={}, 메시지={}", request.getRequestURI(), ex.getMessage(), ex);

        // API 요청인지 확인
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "SQL Grammar Error");
            response.put("message", ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // HTML 요청 처리
        model.addAttribute("orders", List.of());
        model.addAttribute("totalPages", 1);
        model.addAttribute("currentPage", 1);
        model.addAttribute("size", 10);
        model.addAttribute("status", null);
        model.addAttribute("sortBy", "createdAt");
        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("pageRange", new int[]{1});
        return "admin/order/orderList";
    }

    @ExceptionHandler(NoticeNotFoundException.class)
    public Object handleNoticeNotFoundException(NoticeNotFoundException e, Model model, HttpServletRequest request) {
        log.error("공지사항 조회 실패: URL={}, 메시지={}", request.getRequestURI(), e.getMessage(), e);

        // API 요청인지 확인
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Notice Not Found");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // HTML 요청 처리
        model.addAttribute("error", e.getMessage());
        return "404-error";
    }

    @ExceptionHandler(NoSuchElementException.class)
    public Object handleNoSuchElementException(NoSuchElementException e, Model model, HttpServletRequest request) {
        log.error("요소 조회 실패: URL={}, 메시지={}", request.getRequestURI(), e.getMessage(), e);

        // API 요청인지 확인
        if (request.getRequestURI().startsWith("/api/")) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Element Not Found");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // HTML 요청 처리
        model.addAttribute("error", e.getMessage());
        return "404-error";
    }
}