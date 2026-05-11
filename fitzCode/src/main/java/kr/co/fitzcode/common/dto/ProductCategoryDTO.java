package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "상품 카테고리 정보")
public class ProductCategoryDTO {
    @Schema(description = "카테고리 ID")
    private Long categoryId;

    @Schema(description = "상위 카테고리 ID")
    private Long parentId;

    @Schema(description = "카테고리 코드")
    private String code;

    @Schema(description = "카테고리 이름")
    private String name;

    @Schema(description = "카테고리 깊이")
    private Integer depth;
}