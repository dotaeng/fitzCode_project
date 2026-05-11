package kr.co.fitzcode.user.mapper;

import kr.co.fitzcode.common.dto.AddressDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AddressMapper {

    // 사용자 주소록(기본 배송지X) 가져오기
    List<AddressDTO> getUserAddress(@Param("userId") int userId);

    // 기존의 기본배송지 -> 일반배송지로
    void toUnDefaultAddress(@Param("userId") int userId);

    // 입력한 주소 저장
    void insertAddress(@Param("addressDTO") AddressDTO addressDTO, @Param("userId") int userId);

    // 기존의 일반 배송지 -> 기본배송지로
    void toDefaultAddress(@Param("addressId") int addressId);

    // 주소 삭제
    void deleteAddress(@Param("addressIdList") List<Integer> addressIdList);
}
