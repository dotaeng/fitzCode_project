package kr.co.fitzcode.admin.service;

import java.time.LocalDate;
import java.util.Map;

public interface DashboardService {
    // 1대1 문의 관련
    Map<String, Integer> getInquiryStatusCounts();

    // 주간 신규 가입자 수 관련 (날짜 정보 포함)
    Map<LocalDate, Integer> getWeeklyNewUsers();

    // 주간 방문자 수 관련 (날짜 정보 포함)
    Map<LocalDate, Integer> getWeeklyVisitors();

    // 월별 총 매출 관련 (날짜 정보 포함)
    Map<LocalDate, Double> getMonthlyTotalSales();

    // 오늘의 총 주문 수
    int getTodayOrdersCount();

    // 오늘의 총 매출
    Integer getTodayTotalSales();

    // 현재 접속자 수 (WebSocket 기반, 임시 기본값)
    int getCurrentVisitors();

    // 최근 24시간 동안의 취소/반품 요청 수
    int getCancelReturnsCount();

    // 등급별 회원 수 조회
    Map<Integer, Integer> getMemberTierCounts();

    // 세션 갱신 (현재 접속자수)
    void updateUserActivity(String sessionId);
}