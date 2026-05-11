    package kr.co.fitzcode.order.service;

    import kr.co.fitzcode.common.dto.AddressDTO;
    import kr.co.fitzcode.product.service.ProductService;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;

    @Service
    @RequiredArgsConstructor
    public class PriceService {
        private final ProductService productService;

        public int calculatePriceByProductId(int productId, int couponId) {
    //        return productService.getPriceOfProductByProductId(productId);
            return productService.getPriceOfProductWithCoupon(productId, couponId);
        }
    }
