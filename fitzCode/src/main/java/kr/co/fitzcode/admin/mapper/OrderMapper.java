package kr.co.fitzcode.admin.mapper;

import kr.co.fitzcode.common.dto.OrderDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    // 해당 상품의 구매 내역을 페이지 단위로 조회
    List<OrderDetailDTO> findOrderDetailsByProductIdWithPagination(
            @Param("productId") Long productId,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 상품의 전체 구매 내역 수를 조회
    int countOrderDetailsByProductId(@Param("productId") Long productId);
}