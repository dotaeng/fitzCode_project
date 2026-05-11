package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

@Getter
@Setter
@Schema(description = "쿠폰 정보")
public class CouponDTO {
    @Schema(description = "쿠폰 아이디")
    private int couponId;

    @Schema(description = "쿠폰 코드")
    private String couponCode;

    @Schema(description = "쿠폰 설명")
    private String description;

    @Schema(description = "할인 금액")
    private int discountAmount;

    @Schema(description = "할인 비율")
    private int discountPercentage;

    @Schema(description = "최소주문 금액")
    private Integer minimumOrderAmount;

    @Schema(description = "사용가능 기한")
    private Timestamp validUntil;

    @Schema(description = "쿠폰 유형")
    private int couponType;

    @Schema(description = "만료 날짜")
    private Timestamp expiryDate;

    @Schema(description = "쿠폰 활성화 여부")
    private Boolean isActive;

    @Schema(description = "생성 날짜")
    private Timestamp createdAt;

    @Schema(description = "적용 가능한 카테고리 (JSON 문자열)")
    private String applicableCategories;

    // 최소주문금액 -> JSON 직렬화에서 제외
    @JsonIgnore
    public String getFormattedMinimumOrderAmount() {
        DecimalFormat df = new DecimalFormat("#,###원");
        return minimumOrderAmount != null ? df.format(minimumOrderAmount) : null;
    }

    // 사용가능기한
    @JsonIgnore
    public String getFormattedValidUntil() {
        if (validUntil != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format(validUntil);
        }
        return "";
    }

    // 만료 날짜
    @JsonIgnore
    public String getFormattedExpiryDate() {
        if (expiryDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format(expiryDate);
        }
        return "";
    }

    // 생성 날짜
    @JsonIgnore
    public String getFormattedCreatedAt() {
        if (createdAt != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format(createdAt);
        }
        return "";
    }
}