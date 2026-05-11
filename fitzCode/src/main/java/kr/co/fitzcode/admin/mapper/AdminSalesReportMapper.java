package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.SalesRankingDTO;
import kr.co.fitzcode.common.dto.SearchRankingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdminSalesReportMapper {

    // 특정 기간 동안의 일별 매출 데이터 조회
    List<Map<String, Object>> getDailySalesData(@Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    // 최근 14일 동안의 매출 순위 조회 (페이징 지원)
    List<SalesRankingDTO> getSalesRankingForLast14Days(@Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate,
                                                       @Param("offset") int offset);

    // 특정 기간 동안의 총 매출 순위 개수 조회
    int getTotalSalesRankingCount(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    // 검색어 순위 상위 10개 조회
    List<SearchRankingDTO> getSearchRanking();

    // 오늘 검색된 고유 키워드 수 조회
    int getTotalSearchRankingCount();

    // 이전 날짜의 순위 조회 (search_ranking_history에서)
    Integer getPreviousRanking(@Param("keyword") String keyword, @Param("date") LocalDate date);

    // 특정 기간 동안의 총 매출 조회
    Long getTotalIncome(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);
}