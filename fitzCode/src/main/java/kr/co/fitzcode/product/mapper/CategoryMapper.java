package kr.co.fitzcode.product.mapper;

import kr.co.fitzcode.common.dto.CategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDTO> getAllCategories();
}
