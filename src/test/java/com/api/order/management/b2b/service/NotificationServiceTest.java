package com.api.order.management.b2b.service;

import com.api.order.management.b2b.dto.OrderDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Spy
    private NotificationService notificationService;

    @Test
    @DisplayName("1. Deve executar o envio simulado de notificações sem estourar exceções")
    void shouldExecuteNotificationSuccessfully() {
        var orderDto = OrderDTO.builder()
                .id(123L)
                .partnerId(99L)
                .status(OrderStatus.APPROVED)
                .build();

        notificationService.sendStatusNotification(orderDto);

        verify(notificationService, times(1)).sendStatusNotification(orderDto);
    }
}
