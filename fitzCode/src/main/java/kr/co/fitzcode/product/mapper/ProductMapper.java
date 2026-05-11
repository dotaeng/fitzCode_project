package kr.co.fitzcode.product.mapper;

import kr.co.fitzcode.common.dto.ProductDTO;
import kr.co.fitzcode.common.dto.ProductImageDTO;
import kr.co.fitzcode.common.dto.ProductSizeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<ProductDTO> getProductsByPage(int offset);

    List<ProductDTO> getAllProducts();

    ProductDTO getProductById(int productId);

    void insertProduct(ProductDTO productDTO);

    void insertManyProducts(List<ProductDTO> productDTOList);

    void updateProduct(ProductDTO productDTO);

    void deleteProductById(int productId);

    int getNumberOfProducts();


    List<ProductDTO> getProductsByFilter(@Param("codes") List<String> categoryCodes,
                                         @Param("searchText") String searchText,
                                         @Param("offset") int offset);

    int getProductsCountByFilter(@Param("codes") List<String> categoryCodes,
                                 @Param("searchText") String searchText);

    int getCountOfAllProducts();

    List<ProductImageDTO> getProductImagesByProductId(int productId);

    List<ProductSizeDTO> getAllSizeOfProduct(int productId);

    int getProductSizeIdByProductSizeAndCode(@Param("productId") int productId, @Param("sizeCode") int sizeCode);

    int getPriceOfProductByProductId(int productId);

    int getPriceOfProductWithCoupon(int productId, int couponId);

    Long getParentCategoryId(Long categoryId);
}
