package kr.co.fitzcode.product.service;


import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.dto.ProductImageDTO;
import kr.co.fitzcode.common.dto.ProductSizeDTO;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<ProductDTO> getProductsByPage(int pageNum);
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(int productId);
    void insertProduct(ProductDTO productDTO);
    void insertManyProducts(List<ProductDTO> productDTOList);
    void updateProduct(ProductDTO productDTO);
    void updateProductById(int productId);
    void deleteProductById(int productId);

    int getNumberOfPages();

    List<ProductDTO> getProductsByFilterAndPage(List<String> filters, String searchText, int currentPage);
    int getProductsCountByFilter(List<String> filters, String searchText);
    int getCountOfAllProducts();

    List<ProductImageDTO> getProductImagesByProductId(int productId);

    List<ProductSizeDTO> getAllSizeOfProduct(int productId);

    int getProductSizeIdByProductSizeAndCode(int productId, int sizeCode);

    int getPriceOfProductByProductId(int productId);

    int getPriceOfProductWithCoupon(int productId, int couponId);

    Long getParentCategoryId(Long categoryId);

}
