package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "차트 데이터 정보")
public class ChartDataDTO {
    @Schema(description = "30일간 총 매출")
    private List<Integer> revenueData;

    @Schema(description = "30일간 총 판매 건수")
    private List<Integer> volumeData;

    public ChartDataDTO() {
        this.revenueData = new ArrayList<>(Collections.nCopies(30, 0));
        this.volumeData = new ArrayList<>(Collections.nCopies(30, 0));
    }
}