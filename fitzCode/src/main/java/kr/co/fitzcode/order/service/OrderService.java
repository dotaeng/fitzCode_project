package kr.co.fitzcode.order.service;

import kr.co.fitzcode.common.dto.AddressDTO;
import kr.co.fitzcode.common.dto.OrderDTO;
import kr.co.fitzcode.common.dto.PaymentDTO;
import kr.co.fitzcode.common.dto.UserOrderDTO;

import java.util.List;

public interface OrderService {
    List<AddressDTO> getUserAddress(int userId);

    boolean checkIfAddressExistsForUser(int userId, String addressLine1, String postalCode);

    void addNonDefaultAddressForUser(int userId, AddressDTO addressDTO);

    int getAddressIdUsingAddressLine1AndPostalCode(String addressLine1, String postalCode, int userId);

    int insertNewOrder(OrderDTO orderDTO);

    void addPayment(PaymentDTO paymentDTO);

    UserOrderDTO getOrderByOrderId(int orderId);

    AddressDTO getUserAddressByAddressId(int addressId);

    // 사용자가 특정 제품을 구매했는지 확인
    boolean hasPurchasedProduct(int userId, Long productId);
}