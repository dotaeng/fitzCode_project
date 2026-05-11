package kr.co.fitzcode.order.controller;

import kr.co.fitzcode.common.util.SecurityUtils;
import kr.co.fitzcode.order.service.PriceService;
import kr.co.fitzcode.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PriceService priceService;
    @Value("${portone.channel_key}")
    private String channelKey;


    @PostMapping("/getPostOneInfo")
    public ResponseEntity<Map<String, Object>> getPostOneInfo(@RequestParam("totalPrice") int totalPrice) {
        Map<String, Object> response = new HashMap<>();
        // TODO encrypt channel key
        response.put("channelKey", channelKey);
        response.put("payMethod", "card");

        int userId = SecurityUtils.getUserId();
        String date = LocalDate.now().toString();
        String orderInformation = userId + "-" + date;

        // TODO add payment id to payment table
        String merchant_uid = orderInformation + "-" +UUID.randomUUID().toString();
        response.put("merchant_uid", merchant_uid);
        response.put("name", orderInformation);
        response.put("amount", totalPrice);

        return ResponseEntity.ok(response);
    }
}
