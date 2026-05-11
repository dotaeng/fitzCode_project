package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.VisitorDTO;
import org.apache.ibatis.annotations.Mapper;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Mapper
public interface DashboardMapper {
    int countByStatus(int status);
    List<Map<String, Object>> getWeeklyNewUsersWithDates();
    List<VisitorDTO> getWeeklyVisitorsWithDates();
    List<Map<String, Object>> getMonthlyTotalSales();
    int getTodayOrdersCount();
    Integer getTodayTotalSales();
    int getCurrentVisitors();
    int getCancelReturnsCount();
    List<Map<String, Object>> getMemberTierCounts();

    // 방문자 로그 삽입
    void insertVisitLog(Long userId, String visitorId, Timestamp visitTime, String pageUrl,
                        String referrerUrl, String userAgent, int deviceType, String ipAddress);

    // 최근 방문 로그 조회 (중복 체크용)
    VisitorDTO findRecentVisitLogByVisitorId(String visitorId);
}