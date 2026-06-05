package com.api.order.management.b2b.service;

import com.api.order.management.b2b.dto.OrderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void sendStatusNotification(final OrderDTO orderDTO) {
        log.info("[MENSAGERIA SIMULADA] --- Notificação enviada para o parceiro {}. Pedido ID: {} alterado para o Status: {}",
                orderDTO.getPartnerId(), orderDTO.getId(), orderDTO.getStatus());
    }
}
