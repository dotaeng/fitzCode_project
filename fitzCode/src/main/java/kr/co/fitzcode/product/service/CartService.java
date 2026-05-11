package kr.co.fitzcode.product.service;

import kr.co.fitzcode.common.dto.CartDTO;
import kr.co.fitzcode.common.dto.CartProductDTO;

import java.util.List;

public interface CartService {
    public void addProductToCart(CartDTO cartDTO);
    public List<CartDTO> getCartProductsByUserId(int userId);

    List<CartProductDTO> getCartInformationByUserId(int userId);

    int getCartCount(int userId);

    void deleteByCartId(int cartId);
}
