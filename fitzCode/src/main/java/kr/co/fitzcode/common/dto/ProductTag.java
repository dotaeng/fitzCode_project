package kr.co.fitzcode.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductTag {
    private Long productId;
    private String productName;
    private String imageUrl; // 메인 이미지 URL
}

// 상품 태그용 DTO
