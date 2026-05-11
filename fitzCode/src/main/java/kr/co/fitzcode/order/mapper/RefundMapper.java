package kr.co.fitzcode.order.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RefundMapper {
    void requestRefund(@Param("orderDetailId") int orderDetailId);
}
