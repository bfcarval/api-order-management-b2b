package com.api.order.management.b2b.db;

import com.api.order.management.b2b.db.repository.OrderRepository;
import com.api.order.management.b2b.dto.OrderItemDTO;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.mapper.OrderItemMapper;
import com.api.order.management.b2b.model.OrderItemModel;
import com.api.order.management.b2b.model.OrderModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderDbTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderDb orderDb;

    @Test
    @DisplayName("1. Deve salvar um pedido com sucesso e retornar o modelo persistido")
    void shouldSaveOrderSuccessfully() {
        var partnerDto = PartnerDTO.builder().id(1L).name("Parceiro").build();
        var itemDto = OrderItemDTO.builder().product("Item A").build();
        var itemList = List.of(itemDto);
        var totalValue = new BigDecimal("150.00");

        var mockItemModel = mock(OrderItemModel.class);
        var expectedOrder = OrderModel.builder()
                .id(100L)
                .partnerId(1L)
                .status(OrderStatus.PENDING)
                .totalValue(totalValue)
                .build();

        try (MockedStatic<OrderItemMapper> mapperMock = mockStatic(OrderItemMapper.class)) {
            mapperMock.when(() -> OrderItemMapper.fromDTOToModel(any(OrderItemDTO.class))).thenReturn(mockItemModel);
            when(orderRepository.save(any(OrderModel.class))).thenReturn(expectedOrder);

            var result = orderDb.save(itemList, partnerDto, totalValue);

            assertNotNull(result);
            assertEquals(100L, result.getId());
            assertEquals(OrderStatus.PENDING, result.getStatus());
            verify(orderRepository, times(1)).save(any(OrderModel.class));
        }
    }

    @Test
    @DisplayName("2. Deve estourar DatabaseException se o repositório falhar ao salvar")
    void shouldThrowDatabaseExceptionWhenSaveFails() {
        var partnerDto = PartnerDTO.builder().id(1L).build();
        var totalValue = BigDecimal.ZERO;

        when(orderRepository.save(any(OrderModel.class))).thenThrow(new RuntimeException("Conexão perdida"));

        var exception = assertThrows(DatabaseException.class, () ->
                orderDb.save(Collections.emptyList(), partnerDto, totalValue)
        );

        assertEquals("Erro interno ao salvar o pedido no banco de dados.", exception.getMessage());
    }

    @Test
    @DisplayName("3. Deve atualizar um pedido com sucesso")
    void shouldUpdateOrderSuccessfully() {
        var orderModel = OrderModel.builder().id(50L).status(OrderStatus.APPROVED).build();
        when(orderRepository.save(orderModel)).thenReturn(orderModel);

        var result = orderDb.update(orderModel);

        assertNotNull(result);
        assertEquals(50L, result.getId());
        verify(orderRepository, times(1)).save(orderModel);
    }

    @Test
    @DisplayName("4. Deve estourar DatabaseException se o repositório falhar na atualização")
    void shouldThrowDatabaseExceptionWhenUpdateFails() {
        var orderModel = OrderModel.builder().id(50L).build();
        when(orderRepository.save(orderModel)).thenThrow(new RuntimeException("Erro de constraint"));

        var exception = assertThrows(DatabaseException.class, () -> orderDb.update(orderModel));

        assertEquals("Erro interno ao atualizar dados do pedido.", exception.getMessage());
    }

    @Test
    @DisplayName("5. Deve retornar o pedido com itens ao buscar por um ID válido")
    void shouldReturnOrderWhenFoundByIdWithItems() {
        var orderId = 1L;
        var expectedOrder = OrderModel.builder().id(orderId).build();
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.of(expectedOrder));

        var result = orderDb.findByIdWithItems(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    @DisplayName("6. Deve estourar ResourceNotFoundException se o pedido por ID não existir")
    void shouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        var orderId = 999L;
        when(orderRepository.findByIdWithItems(orderId)).thenReturn(Optional.empty());

        var exception = assertThrows(ResourceNotFoundException.class, () -> orderDb.findByIdWithItems(orderId));

        assertEquals("Pedido não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("7. Deve estourar DatabaseException caso ocorra um erro genérico na busca por ID")
    void shouldThrowDatabaseExceptionWhenFindByIdFails() {
        var orderId = 1L;
        when(orderRepository.findByIdWithItems(orderId)).thenThrow(new RuntimeException("Timeout"));

        var exception = assertThrows(DatabaseException.class, () -> orderDb.findByIdWithItems(orderId));

        assertEquals("Erro ao processar consulta de pedido com itens.", exception.getMessage());
    }

    @Test
    @DisplayName("8. Deve retornar uma página de pedidos filtrada por status")
    void shouldReturnPaginatedOrdersByStatus() {
        var status = OrderStatus.PENDING;
        var pageable = PageRequest.of(0, 10);
        var expectedPage = new PageImpl<>(List.of(OrderModel.builder().id(10L).build()));

        when(orderRepository.findByStatus(status, pageable)).thenReturn(expectedPage);

        var result = orderDb.findByStatus(status, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findByStatus(status, pageable);
    }

    @Test
    @DisplayName("9. Deve retornar uma página de pedidos filtrada por intervalo de datas")
    void shouldReturnPaginatedOrdersByDateInterval() {
        var start = LocalDateTime.now().minusDays(5);
        var end = LocalDateTime.now();
        var pageable = PageRequest.of(0, 5);
        var expectedPage = new PageImpl<>(List.of(OrderModel.builder().id(20L).build()));

        when(orderRepository.findByCreatedAtBetween(start, end, pageable)).thenReturn(expectedPage);

        var result = orderDb.findByCreatedAtBetween(start, end, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(orderRepository, times(1)).findByCreatedAtBetween(start, end, pageable);
    }
}
