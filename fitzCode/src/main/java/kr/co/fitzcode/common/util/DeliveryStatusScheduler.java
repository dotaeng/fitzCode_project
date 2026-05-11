package kr.co.fitzcode.common.util;

import kr.co.fitzcode.admin.service.AdminOrderService;
import kr.co.fitzcode.common.dto.DeliveryDTO;
import kr.co.fitzcode.common.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DeliveryStatusScheduler {

    private final AdminOrderService orderService;

    @Scheduled(cron = "0 0 0,6 * * ?")
    public void updateDeliveryStatus() {
        List<DeliveryDTO> deliveries = orderService.getAllDeliveriesInShipping();
        for (DeliveryDTO delivery : deliveries) {
            if (delivery.getShippedAt() != null &&
                    delivery.getShippedAt().plusDays(7).isBefore(LocalDateTime.now())) {
                delivery.setDeliveryStatus(DeliveryStatus.DELIVERED); // 배송완료
                delivery.setDeliveredAt(LocalDateTime.now()); // 배송 완료 날짜
                orderService.updateDelivery(delivery);
            }
        }
    }
}