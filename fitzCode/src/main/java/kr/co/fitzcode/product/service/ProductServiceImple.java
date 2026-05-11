package kr.co.fitzcode.product.service;

import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.dto.ProductImageDTO;
import kr.co.fitzcode.common.dto.ProductSizeDTO;
import kr.co.fitzcode.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImple implements ProductService {

    private final ProductMapper productMapper;
    private final int PRODUCT_PER_PAGE = 20;

    @Override
    public List<ProductDTO> getProductsByPage(int currentPage) {

        int offset = (currentPage - 1) * PRODUCT_PER_PAGE;
        return productMapper.getProductsByPage(offset);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productMapper.getAllProducts();
    }

    @Override
    public ProductDTO getProductById(int productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public void insertProduct(ProductDTO productDTO) {
        productMapper.insertProduct(productDTO);
    }

    @Override
    public void insertManyProducts(List<ProductDTO> productDTOList) {
        productMapper.insertManyProducts(productDTOList);
    }

    @Override
    public void updateProduct(ProductDTO productDTO) {
        productMapper.updateProduct(productDTO);
    }

    @Override
    public void updateProductById(int productId) {
        ProductDTO productDTO = productMapper.getProductById(productId);
        this.updateProduct(productDTO);
    }

    @Override
    public void deleteProductById(int productId) {
        productMapper.deleteProductById(productId);
    }

    @Override
    public int getNumberOfPages() {
        int products = productMapper.getNumberOfProducts();
        return (products/PRODUCT_PER_PAGE);
    }



    @Override
    public int getProductsCountByFilter(List<String> filters, String searchText) {
        return productMapper.getProductsCountByFilter(filters, searchText);
    }

    @Override
    public int getCountOfAllProducts() {
        return productMapper.getCountOfAllProducts();
    }

    @Override
    public List<ProductImageDTO> getProductImagesByProductId(int productId) {
        return productMapper.getProductImagesByProductId(productId);
    }

    @Override
    public List<ProductSizeDTO> getAllSizeOfProduct(int productId) {
        return productMapper.getAllSizeOfProduct(productId);
    }

    @Override
    public int getProductSizeIdByProductSizeAndCode(int productId, int sizeCode) {
        return productMapper.getProductSizeIdByProductSizeAndCode(productId, sizeCode);
    }

    @Override
    public int getPriceOfProductByProductId(int productId) {
        return productMapper.getPriceOfProductByProductId(productId);
    }

    @Override
    public int getPriceOfProductWithCoupon(int productId, int couponId) {
        return productMapper.getPriceOfProductWithCoupon(productId, couponId);
    }

    @Override
    public List<ProductDTO> getProductsByFilterAndPage(List<String> filters, String searchText, int currentPage) {

        int offset = (currentPage - 1) * PRODUCT_PER_PAGE;

        log.info("filters: {}, searchtext: {}, int page: {}", filters, searchText, currentPage);

        if ((filters == null || filters.isEmpty()) && (searchText == null || searchText.isEmpty())) {
            log.info("getProductsByFilterAndPage: filters or searchText is empty........ getting all products");
            return productMapper.getProductsByPage(offset);
        } else {
            log.info("getProductsByFilterAndPage: ....... getting some products");
            log.info("filters... should not be null: {}, searchtext: {}", filters, searchText);
            log.info("filters type: {}", filters.getClass().getName());

            return productMapper.getProductsByFilter(filters, searchText, offset);
        }
    }

    @Override
    public Long getParentCategoryId(Long categoryId) {
        return productMapper.getParentCategoryId(categoryId); // 매퍼에서 상위 카테고리 조회
    }

}
