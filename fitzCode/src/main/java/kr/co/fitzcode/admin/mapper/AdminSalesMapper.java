package kr.co.fitzcode.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AdminSalesMapper {

    // 최근 기간의 날짜 레이블을 조회
    List<String> getDailyLabels(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 최근 기간의 일별 매출액을 조회
    List<Integer> getDailySalesAmount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 최근 기간의 일별 판매 수량을 조회
    List<Integer> getDailySalesQuantity(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}