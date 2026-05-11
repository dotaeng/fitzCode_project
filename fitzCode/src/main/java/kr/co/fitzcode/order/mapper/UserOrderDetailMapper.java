package kr.co.fitzcode.order.mapper;

import kr.co.fitzcode.common.dto.UserOrderDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserOrderDetailMapper {

    void batchInsertOrderDetails(@Param("orderDetails") List<Map<String, Object>> orderDetails);


    List<UserOrderDetailDTO> getOrderDetailByOrderId(int orderId);

    List<UserOrderDetailDTO> getOrderDetailByUserId(int userId);

    void updateRequestRefundStatus(int orderDetailId);

    void decrementProductCountInProductSize(int productId, int sizeCode);

    void decrementProductCountInProduct(int productId);

}
