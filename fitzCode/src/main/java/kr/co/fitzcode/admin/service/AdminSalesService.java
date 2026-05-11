package kr.co.fitzcode.admin.service;

import kr.co.fitzcode.common.dto.ChartDataDTO;
import kr.co.fitzcode.common.dto.SalesRankingDTO;
import kr.co.fitzcode.common.dto.SearchRankingDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminSalesService {
    ChartDataDTO getSalesDataForLast30Days();
    List<String> getDailyLabels();
    List<SalesRankingDTO> getSalesRankingForLast14Days(int page, int size);
    List<SalesRankingDTO> getSalesRankingForLast14Days();
    int getTotalSalesRankingCount();

    List<SearchRankingDTO> getSearchRanking(); // 검색어 순위 상위 10개 반환
    int getTotalSearchRankingCount(); // 오늘 검색된 고유 키워드 수 반환

    // 특정 기간 동안의 총 매출 계산
    long getTotalIncome(LocalDateTime startDate, LocalDateTime endDate);

    // 지난달 대비 매출 증감률 계산
    double calculateExpenseGrowthRate(long lastMonthIncome, long thisMonthIncome);

    // 지난달 대비 증가율 계산 (Total Income의 증가율 표시용)
    double calculateIncomeGrowthRate(long lastMonthIncome, long thisMonthIncome);

    // 이번 달 예상 매출 계산
    long calculatePredictedIncome(LocalDateTime startOfThisMonth, LocalDateTime endOfThisMonth, long totalIncome, LocalDate today);
}