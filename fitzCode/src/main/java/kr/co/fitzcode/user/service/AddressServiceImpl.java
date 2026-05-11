package kr.co.fitzcode.user.service;

import kr.co.fitzcode.common.dto.AddressDTO;
import kr.co.fitzcode.user.mapper.AddressMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressMapper addressMapper;
    // 사용자 주소록 가져오기
    @Override
    public List<AddressDTO> getUserAddress(int userId) {
        List <AddressDTO> addressList = addressMapper.getUserAddress(userId);
        return addressList;
    }

    // 주소 추가
    @Transactional
    @Override
    public void insertAddress(AddressDTO addressDTO, int userId) {
        if (addressDTO.isDefault()){ // 기본 배송지로 체크했다면
            addressMapper.toUnDefaultAddress(userId); // 기존의 기본배송지 -> 일반배송지로
        }
        addressMapper.insertAddress(addressDTO, userId);
    }

    // 기존의 일반 배송지 -> 기본 배송지
    @Transactional
    @Override
    public void toDefaultAddress(int addressId, int userId) {
        addressMapper.toUnDefaultAddress(userId); // 기존의 기본배송지 -> 일반배송지
        addressMapper.toDefaultAddress(addressId); // 기존의 일반배송지 -> 기본배송지

    }

    // 주소 삭제
    @Override
    public void deleteAddress(List<Integer> addressIdList) {
        addressMapper.deleteAddress(addressIdList);
    }
}
