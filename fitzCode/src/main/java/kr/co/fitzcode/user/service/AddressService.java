package kr.co.fitzcode.user.service;

import kr.co.fitzcode.common.dto.AddressDTO;

import java.util.List;

public interface AddressService {
    // 사용자 주소록 가져오기
    List<AddressDTO> getUserAddress(int userId);

    // 주소 추가
    void insertAddress(AddressDTO addressDTO, int userId);

    // 기존의 일반배송지 -> 기본 배송지
    void toDefaultAddress(int addressId, int userId);

    // 주소 삭제
    void deleteAddress(List<Integer> addressIdList);
}


