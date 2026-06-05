package com.api.order.management.b2b.service;

import com.api.order.management.b2b.controller.request.OrderItemRequest;
import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.db.OrderDb;
import com.api.order.management.b2b.db.PartnerDb;
import com.api.order.management.b2b.dto.OrderDTO;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.exception.BusinessException;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.mapper.OrderItemMapper;
import com.api.order.management.b2b.mapper.OrderMapper;
import com.api.order.management.b2b.mapper.PartnerMapper;
import com.api.order.management.b2b.model.OrderModel;
import com.api.order.management.b2b.model.PartnerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDb orderDb;

    @Mock
    private PartnerDb partnerDb;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest sampleRequest;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        idempotencyKey = "key-123";
        OrderItemRequest item = new OrderItemRequest("Persiana", 2, new BigDecimal("50.00"));
        sampleRequest = new OrderRequest(1L, List.of(item));
    }

    @Nested
    @DisplayName("1. Criação de Pedidos")
    class CreateOrderTests {

        @Test
        void createOrderSuccess() {
            var partnerModel = mock(PartnerModel.class);
            var orderModel = mock(OrderModel.class);

            when(partnerDb.findById(1L)).thenReturn(partnerModel);
            when(orderDb.save(any(), any(), any())).thenReturn(orderModel);

            try (MockedStatic<PartnerMapper> partnerMapper = mockStatic(PartnerMapper.class);
                 MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class);
                 MockedStatic<OrderItemMapper> itemMapper = mockStatic(OrderItemMapper.class)) {

                var partnerDTO = mock(PartnerDTO.class);
                var orderDTO = mock(OrderDTO.class);

                partnerMapper.when(() -> PartnerMapper.fromModelToDTO(partnerModel)).thenReturn(partnerDTO);
                when(partnerDTO.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));

                itemMapper.when(() -> OrderItemMapper.mapItemsFromRequest(sampleRequest)).thenReturn(Collections.emptyList());
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);
                when(orderDTO.getId()).thenReturn(99L);

                OrderDTO result = orderService.createOrder(idempotencyKey, sampleRequest);

                assertNotNull(result);
                assertEquals(99L, result.getId());
                verify(notificationService).sendStatusNotification(orderDTO);
            }
        }


        @Test
        void createOrderDuplicateIdempotencyKey() {
            var partnerModel = mock(PartnerModel.class);
            var orderModel = mock(OrderModel.class);

            when(partnerDb.findById(1L)).thenReturn(partnerModel);
            when(orderDb.save(any(), any(), any())).thenReturn(orderModel);

            try (MockedStatic<PartnerMapper> partnerMapper = mockStatic(PartnerMapper.class);
                 MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class);
                 MockedStatic<OrderItemMapper> itemMapper = mockStatic(OrderItemMapper.class)) {

                var partnerDTO = mock(com.api.order.management.b2b.dto.PartnerDTO.class);
                var orderDTO = mock(OrderDTO.class);

                partnerMapper.when(() -> PartnerMapper.fromModelToDTO(partnerModel)).thenReturn(partnerDTO);
                when(partnerDTO.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));
                itemMapper.when(() -> OrderItemMapper.mapItemsFromRequest(sampleRequest)).thenReturn(Collections.emptyList());
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                orderService.createOrder(idempotencyKey, sampleRequest);

                BusinessException exception = assertThrows(BusinessException.class, () ->
                        orderService.createOrder(idempotencyKey, sampleRequest)
                );

                assertEquals("Esta transação já foi processada ou está em andamento.", exception.getMessage());
            }
        }


        @Test
        void createOrderInsufficientCredit() {
            var partnerModel = mock(PartnerModel.class);
            when(partnerDb.findById(1L)).thenReturn(partnerModel);
            try (MockedStatic<PartnerMapper> partnerMapper = mockStatic(PartnerMapper.class)) {
                var partnerDTO = mock(PartnerDTO.class);
                partnerMapper.when(() -> PartnerMapper.fromModelToDTO(partnerModel)).thenReturn(partnerDTO);
                when(partnerDTO.getCreditLimit()).thenReturn(new BigDecimal("50.00"));

                assertThrows(BusinessException.class, () -> orderService.createOrder(idempotencyKey, sampleRequest));
            }

        }

        @Test
        void createOrderResourceNotFoundException() {
            when(partnerDb.findById(1L)).thenThrow(new ResourceNotFoundException("Not Found"));
            assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(idempotencyKey, sampleRequest));
        }

        @Test
        void createOrderDatabaseException() {
            when(partnerDb.findById(1L)).thenThrow(new DatabaseException("DB Error"));
            assertThrows(DatabaseException.class, () -> orderService.createOrder(idempotencyKey, sampleRequest));
        }

        @Test
        void createOrderUnexpectedException() {
            when(partnerDb.findById(1L)).thenThrow(new RuntimeException("Unexpected"));
            assertThrows(BusinessException.class, () -> orderService.createOrder(idempotencyKey, sampleRequest));
        }
    }

    @Nested
    @DisplayName("2. Atualização de Status")
    class UpdateStatusTests {

        @Test
        void updateStatusToCanceledThrowsException() {
            var orderModel = mock(OrderModel.class);
            when(orderModel.getId()).thenReturn(1L);
            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);

            assertThrows(BusinessException.class, () -> orderService.updateStatus(1L, OrderStatus.CANCELED));
        }

        @Test
        void updateStatusSameStatusThrowsException() {
            var orderModel = mock(OrderModel.class);
            when(orderModel.getId()).thenReturn(1L);
            when(orderModel.getStatus()).thenReturn(OrderStatus.APPROVED);
            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);

            assertThrows(BusinessException.class, () -> orderService.updateStatus(1L, OrderStatus.APPROVED));
        }

        @Test
        void updateStatusFromPendingToApprovedSuccess() {
            var orderModel = mock(OrderModel.class);
            var partnerModel = mock(PartnerModel.class);

            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);
            when(orderModel.getStatus()).thenReturn(OrderStatus.PENDING);
            when(orderModel.getTotalValue()).thenReturn(new BigDecimal("200.00"));
            when(orderModel.getPartnerId()).thenReturn(5L);
            when(partnerDb.findById(5L)).thenReturn(partnerModel);
            when(partnerModel.getCreditLimit()).thenReturn(new BigDecimal("500.00"));
            when(orderDb.update(orderModel)).thenReturn(orderModel);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                OrderDTO result = orderService.updateStatus(1L, OrderStatus.APPROVED);

                assertNotNull(result);
                verify(partnerModel).setCreditLimit(new BigDecimal("300.00"));
                verify(partnerDb).update(partnerModel);
                verify(orderModel).setStatus(OrderStatus.APPROVED);
                verify(notificationService).sendStatusNotification(orderDTO);
            }
        }

        @Test
        void updateStatusFromPendingToApprovedInsufficientCredit() {
            var orderModel = mock(OrderModel.class);
            var partnerModel = mock(PartnerModel.class);

            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);
            when(orderModel.getStatus()).thenReturn(OrderStatus.PENDING);
            when(orderModel.getTotalValue()).thenReturn(new BigDecimal("600.00"));
            when(orderModel.getPartnerId()).thenReturn(5L);
            when(partnerDb.findById(5L)).thenReturn(partnerModel);
            when(partnerModel.getCreditLimit()).thenReturn(new BigDecimal("500.00"));
            assertThrows(BusinessException.class, () -> orderService.updateStatus(1L, OrderStatus.APPROVED));
        }

        @Test
        void updateStatusOtherTransitions() {
            var orderModel = mock(OrderModel.class);
            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);
            when(orderModel.getStatus()).thenReturn(OrderStatus.APPROVED);
            when(orderDb.update(orderModel)).thenReturn(orderModel);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                OrderDTO result = orderService.updateStatus(1L, OrderStatus.DELIVERED);

                assertNotNull(result);
                verify(orderModel).setStatus(OrderStatus.DELIVERED);
                verify(partnerDb, never()).update(any());
            }
        }

        @Test
        void updateStatusResourceNotFoundException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new ResourceNotFoundException("Not Found"));
            assertThrows(ResourceNotFoundException.class, () -> orderService.updateStatus(1L, OrderStatus.APPROVED));
        }

        @Test
        void updateStatusDatabaseException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new DatabaseException("DB Error"));
            assertThrows(DatabaseException.class, () -> orderService.updateStatus(1L, OrderStatus.APPROVED));
        }

        @Test
        void updateStatusUnexpectedException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new RuntimeException("Unexpected"));
            assertThrows(BusinessException.class, () -> orderService.updateStatus(1L, OrderStatus.APPROVED));
        }
    }

    @Nested
    @DisplayName("3. Cancelamento de Pedidos")
    class CancelOrderTests {

        @Test
        void cancelOrderAlreadyCanceledThrowsException() {
            var orderModel = mock(OrderModel.class);
            when(orderModel.getId()).thenReturn(1L);
            when(orderModel.getStatus()).thenReturn(OrderStatus.CANCELED);
            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);

            assertThrows(BusinessException.class, () -> orderService.cancelOrder(1L));
        }

        @Test
        void cancelOrderPendingSuccess() {
            var orderModel = mock(OrderModel.class);
            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);
            when(orderModel.getStatus()).thenReturn(OrderStatus.PENDING);
            when(orderDb.update(orderModel)).thenReturn(orderModel);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                OrderDTO result = orderService.cancelOrder(1L);

                assertNotNull(result);
                verify(orderModel).setStatus(OrderStatus.CANCELED);
                verify(partnerDb, never()).update(any());
            }
        }

        @Test
        void cancelOrderApprovedWithRefundSuccess() {
            var orderModel = mock(OrderModel.class);
            var partnerModel = mock(PartnerModel.class);

            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);
            when(orderModel.getStatus()).thenReturn(OrderStatus.APPROVED);
            when(orderModel.getTotalValue()).thenReturn(new BigDecimal("150.00"));
            when(orderModel.getPartnerId()).thenReturn(5L);
            when(partnerDb.findById(5L)).thenReturn(partnerModel);
            when(partnerModel.getCreditLimit()).thenReturn(new BigDecimal("1000.00"));
            when(orderDb.update(orderModel)).thenReturn(orderModel);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                OrderDTO result = orderService.cancelOrder(1L);

                assertNotNull(result);
                verify(partnerModel).setCreditLimit(new BigDecimal("1150.00"));
                verify(partnerDb).update(partnerModel);
                verify(orderModel).setStatus(OrderStatus.CANCELED);
            }
        }

        @Test
        void cancelOrderResourceNotFoundException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new ResourceNotFoundException("Not Found"));
            assertThrows(ResourceNotFoundException.class, () -> orderService.cancelOrder(1L));
        }

        @Test
        void cancelOrderDatabaseException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new DatabaseException("DB Error"));
            assertThrows(DatabaseException.class, () -> orderService.cancelOrder(1L));
        }

        @Test
        void cancelOrderUnexpectedException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new RuntimeException("Unexpected"));
            assertThrows(BusinessException.class, () -> orderService.cancelOrder(1L));
        }
    }

    @Nested
    @DisplayName("4. Consultas de Pedidos")
    class GetOrderTests {

        @Test
        void getByIdSuccess() {
            var orderModel = mock(OrderModel.class);
            when(orderDb.findByIdWithItems(1L)).thenReturn(orderModel);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                OrderDTO result = orderService.getById(1L);

                assertNotNull(result);
            }
        }

        @Test
        void getByIdResourceNotFoundException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new ResourceNotFoundException("Not Found"));
            assertThrows(ResourceNotFoundException.class, () -> orderService.getById(1L));
        }

        @Test
        void getByIdDatabaseException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new DatabaseException("DB Error"));
            assertThrows(DatabaseException.class, () -> orderService.getById(1L));
        }

        @Test
        void getByIdUnexpectedException() {
            when(orderDb.findByIdWithItems(1L)).thenThrow(new RuntimeException("Unexpected"));
            assertThrows(BusinessException.class, () -> orderService.getById(1L));
        }

        @Test
        void getByStatusSuccess() {
            Pageable pageable = PageRequest.of(0, 10);
            var orderModel = mock(OrderModel.class);
            Page<OrderModel> page = new PageImpl<>(List.of(orderModel));
            when(orderDb.findByStatus(OrderStatus.PENDING, pageable)).thenReturn(page);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                Page<OrderDTO> result = orderService.getByStatus(OrderStatus.PENDING, pageable);

                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
            }
        }

        @Test
        void getByStatusDatabaseException() {
            Pageable pageable = PageRequest.of(0, 10);
            when(orderDb.findByStatus(OrderStatus.PENDING, pageable)).thenThrow(new DatabaseException("DB Error"));
            assertThrows(DatabaseException.class, () -> orderService.getByStatus(OrderStatus.PENDING, pageable));
        }

        @Test
        void getByStatusUnexpectedException() {
            Pageable pageable = PageRequest.of(0, 10);
            when(orderDb.findByStatus(OrderStatus.PENDING, pageable)).thenThrow(new RuntimeException("Unexpected"));
            assertThrows(BusinessException.class, () -> orderService.getByStatus(OrderStatus.PENDING, pageable));
        }

        @Test
        void getByPeriodSuccess() {
            LocalDateTime start = LocalDateTime.now().minusDays(1);
            LocalDateTime end = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            var orderModel = mock(OrderModel.class);
            Page<OrderModel> page = new PageImpl<>(List.of(orderModel));
            when(orderDb.findByCreatedAtBetween(start, end, pageable)).thenReturn(page);

            try (MockedStatic<OrderMapper> orderMapper = mockStatic(OrderMapper.class)) {
                var orderDTO = mock(OrderDTO.class);
                orderMapper.when(() -> OrderMapper.fromModelToDTO(orderModel)).thenReturn(orderDTO);

                Page<OrderDTO> result = orderService.getByPeriod(start, end, pageable);

                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
            }
        }

        @Test
        void getByPeriodDatabaseException() {
            LocalDateTime start = LocalDateTime.now().minusDays(1);
            LocalDateTime end = LocalDateTime.now();
            Pageable pageable = PageRequest.of(0, 10);
            when(orderDb.findByCreatedAtBetween(start, end, pageable)).thenThrow(new DatabaseException("DB Error"));
            assertThrows(DatabaseException.class, () -> orderService.getByPeriod(start, end, pageable));
        }
    }
}
