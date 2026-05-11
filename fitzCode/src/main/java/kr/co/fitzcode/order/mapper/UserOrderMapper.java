package kr.co.fitzcode.order.mapper;

import kr.co.fitzcode.common.dto.AddressDTO;
import kr.co.fitzcode.common.dto.OrderDTO;
import kr.co.fitzcode.common.dto.PaymentDTO;
import kr.co.fitzcode.common.dto.UserOrderDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserOrderMapper {

    List<AddressDTO> getUserAddressByUserId(int userId);

    boolean checkIfAddressExistsForUser(int userId, String addressLine1, String postalCode);

    void addNonDefaultAddressForUser(@Param("userId") int userId, @Param("addressDTO") AddressDTO addressDTO);

    int getAddressIdUsingAddressLine1AndPostalCode(String addressLine1, String postalCode, int userId);

    int insertNewOrder(OrderDTO orderDTO);

    void addPayment(@Param("paymentDTO") PaymentDTO paymentDTO);

    UserOrderDTO getOrderByOrderId(int orderId);

    AddressDTO getUserAddressByAddressId(int addressId);

    // 사용자가 특정 제품을 구매했는지 확인
    int countOrdersByUserAndProduct(@Param("userId") int userId, @Param("productId") Long productId);
}