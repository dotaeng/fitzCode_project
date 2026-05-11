package kr.co.fitzcode.product.service;

import kr.co.fitzcode.common.dto.CartDTO;
import kr.co.fitzcode.common.dto.CartProductDTO;
import kr.co.fitzcode.product.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImple implements CartService {
    private final CartMapper cartMapper;

    @Override
    public void addProductToCart(CartDTO cartDTO) {
        cartMapper.addProductToCart(cartDTO);
    }

    @Override
    public List<CartDTO> getCartProductsByUserId(int userId) {
        return cartMapper.getCartProductsByUserId(userId);
    }

    @Override
    public List<CartProductDTO> getCartInformationByUserId(int userId) {
        return cartMapper.getCartInformationByUserId(userId);
    }

    @Override
    public int getCartCount(int userId) {
        return cartMapper.getCartCount(userId);
    }

    @Override
    public void deleteByCartId(int cartId) {
        cartMapper.deleteByCartId(cartId);
    }
}
