package kr.co.fitzcode.product.service;

import kr.co.fitzcode.common.dto.CategoryDTO;
import kr.co.fitzcode.product.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImple implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.getAllCategories();
    }
}
