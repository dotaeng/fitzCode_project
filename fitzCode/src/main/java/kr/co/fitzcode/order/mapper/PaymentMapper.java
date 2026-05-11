package kr.co.fitzcode.order.mapper;

import kr.co.fitzcode.common.dto.PaymentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {
    void createPayment(@Param("paymentDTO") PaymentDTO paymentDTO);
}
