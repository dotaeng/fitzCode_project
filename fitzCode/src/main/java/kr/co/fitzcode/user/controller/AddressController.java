package kr.co.fitzcode.user.controller;

import com.nimbusds.openid.connect.sdk.claims.Address;
import kr.co.fitzcode.common.dto.AddressDTO;
import kr.co.fitzcode.common.util.SecurityUtils;
import kr.co.fitzcode.user.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;
    private final SecurityUtils securityUtils;

    // 내정보 - 주소록 리스트
    @GetMapping("/address")
    public String addresslist(Model model) {
        int userId = securityUtils.getUserId();
        List<AddressDTO> addressList = addressService.getUserAddress(userId);
        AddressDTO defaultAddress = addressList.stream().filter(AddressDTO::isDefault).findFirst().orElse(null);
        List<AddressDTO> addressDTOlist = addressList.stream().filter(address -> !address.isDefault()).collect(Collectors.toUnmodifiableList());
        model.addAttribute("userId", userId);
        model.addAttribute("list", addressDTOlist);
        model.addAttribute("default", defaultAddress);
        return "user/mypage/address";
    }

    // 주소 추가 -> 주소 목록으로 리다이렉트
    @PostMapping("/insertAddress")
    public void insertAddress(@RequestBody AddressDTO addressDTO) {
        int userId = securityUtils.getUserId();
        addressService.insertAddress(addressDTO, userId);
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>> 아무메세지 ");
    }

    // 일반 배송지 -> 기본배송지로 설정 -> 주소목록으로 리다이렉트
    @GetMapping("/toDefaultAddress/{addressId}/{userId}")
    public String toDefaultAddress(@PathVariable("addressId") int addressId,
                                   @PathVariable("userId") int userId) {
        addressService.toDefaultAddress(addressId, userId);
        return "redirect:/mypage/address";
    }

    // 주소 삭제
    @PostMapping("/deleteAddress")
    public String deleteAddress(@RequestParam("addressId") List<Integer> addressIdList) {
        addressService.deleteAddress(addressIdList);
        return "redirect:/mypage/address";
    }
}
