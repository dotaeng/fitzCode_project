package kr.co.fitzcode.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리 정보")
public class CategoryDTO {
    @Schema(description = "카테고리 ID")
    private int categoryId;

    @Schema(description = "상위 카테고리 ID")
    private int parentId;

    @Schema(description = "카테고리 코드")
    private String code;

    @Schema(description = "카테고리 이름")
    private String name;

    @Schema(description = "카테고리 깊이")
    private int depth;
}