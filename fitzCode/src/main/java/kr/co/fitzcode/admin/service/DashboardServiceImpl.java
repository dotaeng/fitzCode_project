package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.admin.mapper.DashboardMapper;
import kr.co.fitzcode.common.dto.VisitorDTO;
import kr.co.fitzcode.common.enums.InquiryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {

    private final DashboardMapper dashboardMapper;
    private static final Map<String, Long> activeSessions = new ConcurrentHashMap<>(); // 메모리 내 세션 저장

    // 1대1 문의 현황
    @Override
    public Map<String, Integer> getInquiryStatusCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (InquiryStatus status : InquiryStatus.values()) {
            int count = dashboardMapper.countByStatus(status.getCode());
            counts.put(status.name(), count); // 상태 이름 -> 키 사용
        }
        return counts;
    }

    // 1주일간 신규 가입자 수
    @Override
    public Map<LocalDate, Integer> getWeeklyNewUsers() {
        List<Map<String, Object>> counts = dashboardMapper.getWeeklyNewUsersWithDates();
        Map<LocalDate, Integer> weeklyNewUsers = new TreeMap<>(); // 날짜 순 정렬
        log.debug("신규 가입자 수: {}", counts);

        if (counts != null && !counts.isEmpty()) {
            LocalDate today = LocalDate.now();
            // 최근 7일(6일 전 ~ 오늘) 초기화 (기본값 0)
            for (int i = 6; i >= 0; i--) {
                LocalDate date = today.minusDays(i);
                weeklyNewUsers.put(date, 0);
            }

            // 날짜와 가입자 수 매핑
            for (Map<String, Object> row : counts) {
                LocalDate signupDate = ((java.sql.Date) row.get("signup_date")).toLocalDate();
                Long countLong = (Long) row.get("count"); // 형변환
                Integer count = countLong != null ? countLong.intValue() : 0;
                weeklyNewUsers.put(signupDate, count);
            }
        }
        log.debug("신규 가입자 수: {}", weeklyNewUsers);
        return weeklyNewUsers;
    }

    // 1주일간 방문자 수
    @Override
    public Map<LocalDate, Integer> getWeeklyVisitors() { // 반환 타입 변경
        List<VisitorDTO> counts = dashboardMapper.getWeeklyVisitorsWithDates();
        Map<LocalDate, Integer> weeklyVisitors = new TreeMap<>();
        log.debug("주간 방문자 수 원본 데이터: {}", counts);

        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            weeklyVisitors.put(date, 0);
        }

        if (counts != null && !counts.isEmpty()) {
            for (VisitorDTO dto : counts) {
                weeklyVisitors.compute(dto.getVisitDate(), (key, oldValue) ->
                        (oldValue == null ? 0 : oldValue) + (dto.getCount() != null ? dto.getCount() : 0));
            }
        }

        log.debug("주간 방문자 수: {}", weeklyVisitors);
        return weeklyVisitors;
    }

    // 월별 총 매출
    @Override
    public Map<LocalDate, Double> getMonthlyTotalSales() {
        List<Map<String, Object>> sales = dashboardMapper.getMonthlyTotalSales();
        Map<LocalDate, Double> monthlySales = new TreeMap<>(); // 날짜 순 정렬
        log.debug("월별 총 매출: {}", sales);

        if (sales != null && !sales.isEmpty()) {
            LocalDate today = LocalDate.now();
            // 최근 12개월(11개월 전 ~ 오늘) 초기화 (기본값 0.0)
            for (int i = 11; i >= 0; i--) {
                LocalDate date = today.minusMonths(i).withDayOfMonth(1); // 월의 첫째 날로 설정
                monthlySales.put(date, 0.0);
            }

            // 쿼리 결과에서 날짜와 매출을 매핑
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            for (Map<String, Object> row : sales) {
                String saleMonthStr = (String) row.get("sale_month");
                Double totalSales = (Double) row.get("total_sales"); // 형변환
                monthlySales.put(LocalDate.parse(saleMonthStr + "-01", formatter), totalSales != null ? totalSales : 0.0);
            }
        }
        log.debug("월별 총 매출: {}", monthlySales);
        return monthlySales;
    }

    // 오늘 총 주문 수
    @Override
    public int getTodayOrdersCount() {
        Integer count = dashboardMapper.getTodayOrdersCount();
        return count != null ? count : 0;
    }

    // 오늘 총 매출
    @Override
    public Integer getTodayTotalSales() {
        Integer sales = dashboardMapper.getTodayTotalSales();
        return sales != null ? sales : 0;
    }

    // 현재 접속자 수 (메모리 세션으로)
    @Override
    public int getCurrentVisitors() {
        long threshold = System.currentTimeMillis() - (5 * 60 * 1000); // 5분 이내 활동
        int activeCount = (int) activeSessions.entrySet().stream()
                .filter(entry -> entry.getValue() > threshold)
                .count();
        log.debug("현재 접속자 수: {}", activeCount);
        return activeCount;
    }

    // 세션 갱신하는 메서드 -> 현재 접속자수
    @Override
    public void updateUserActivity(String sessionId) {
        activeSessions.put(sessionId, System.currentTimeMillis());
        log.debug("세션 활동 갱신: sessionId={}", sessionId);
    }

    // 24시간 동안의 취소/반품 요청 수
    @Override
    public int getCancelReturnsCount() {
        Integer count = dashboardMapper.getCancelReturnsCount();
        return count != null ? count : 0;
    }

    // 등급별 회원 수 조회
    @Override
    public Map<Integer, Integer> getMemberTierCounts() {
        List<Map<String, Object>> tierCounts = dashboardMapper.getMemberTierCounts();
        Map<Integer, Integer> memberTierCounts = new HashMap<>();
        log.debug("회원 등급별 인원: {}", tierCounts);

        if (tierCounts != null && !tierCounts.isEmpty()) {
            for (Map<String, Object> row : tierCounts) {
                Integer tierLevel = (Integer) row.get("level");
                Long countLong = (Long) row.get("count");
                Integer count = countLong != null ? countLong.intValue() : 0;
                memberTierCounts.put(tierLevel, count);
            }
        }
        log.debug("등급별 회원 수: {}", memberTierCounts);
        return memberTierCounts;
    }
}