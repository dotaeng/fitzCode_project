package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "검색 순위 정보")
public class SearchRankingDTO {
    @Schema(description = "검색어")
    private String keyword;

    @Schema(description = "오늘 검색 횟수")
    private long todaySearchCount;

    @Schema(description = "현재 순위")
    private int currentRanking;

    @Schema(description = "전일 순위")
    private int previousRanking;

    @Schema(description = "날짜")
    private LocalDate date;

    public String getRankingChange() {
        if (previousRanking == 0) return "-";
        int change = previousRanking - currentRanking;
        return change == 0 ? "-" : (change > 0 ? "+" + change : String.valueOf(change));
    }
}