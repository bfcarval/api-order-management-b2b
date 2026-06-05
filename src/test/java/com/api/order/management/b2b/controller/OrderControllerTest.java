package com.api.order.management.b2b.controller;

import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.controller.request.StatusUpdateRequest;
import com.api.order.management.b2b.controller.response.OrderResponse;
import com.api.order.management.b2b.dto.OrderDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.api.order.management.b2b.mapper.OrderMapper.fromDTOToResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        value = OrderController.class,
        properties = "spring.main.allow-bean-definition-overriding=true"
)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("1. Deve retornar 201 Created ao criar um pedido com sucesso")
    void shouldReturn201WhenOrderCreatedSuccessfully() throws Exception {
        var idempotencyKey = "key-12345";
        var requestBody = "{\"partnerId\": 1, \"items\": [{\"product\": \"Notebook\", \"quantity\": 2, \"unitPrice\": 4500.00}]}";

        var expectedResponse = new OrderResponse(1L, 1L, Collections.emptyList(), new BigDecimal("9000.00"), OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());
        var mockDomainObject = mock(OrderDTO.class);

        when(orderService.createOrder(eq(idempotencyKey), any(OrderRequest.class))).thenReturn(mockDomainObject);

        try (MockedStatic<com.api.order.management.b2b.mapper.OrderMapper> mapperMock = mockStatic(com.api.order.management.b2b.mapper.OrderMapper.class)) {
            mapperMock.when(() -> com.api.order.management.b2b.mapper.OrderMapper.fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(post("/api/orders")
                            .header("X-Idempotency-Key", idempotencyKey)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.totalValue").value(9000.00));
        }
    }

    @Test
    @DisplayName("2. Deve retornar 200 Ok ao buscar um pedido existente por ID")
    void shouldReturn200WhenFindingOrderById() throws Exception {
        var orderId = 1L;
        var expectedResponse = new OrderResponse(orderId, 1L, Collections.emptyList(), new BigDecimal("150.00"), OrderStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());
        var mockDomainObject = mock(OrderDTO.class);

        when(orderService.getById(orderId)).thenReturn(mockDomainObject);

        try (MockedStatic<com.api.order.management.b2b.mapper.OrderMapper> mapperMock = mockStatic(com.api.order.management.b2b.mapper.OrderMapper.class)) {
            mapperMock.when(() -> fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(get("/api/orders/" + orderId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(orderId))
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }
    }

    @Test
    @DisplayName("3. Deve retornar uma página de pedidos filtrada por status")
    void shouldReturnPaginatedOrdersByStatus() throws Exception {
        var status = OrderStatus.APPROVED;
        var mockDomainObject = mock(OrderDTO.class);
        var pageable = PageRequest.of(0, 10);
        var servicePage = new PageImpl<>(List.of(mockDomainObject), pageable, 1);

        var expectedResponse = new OrderResponse(1L, 1L, Collections.emptyList(), new BigDecimal("500.00"), status, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getByStatus(eq(status), any(Pageable.class))).thenReturn(servicePage);

        try (MockedStatic<com.api.order.management.b2b.mapper.OrderMapper> mapperMock = mockStatic(com.api.order.management.b2b.mapper.OrderMapper.class)) {
            mapperMock.when(() -> fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(get("/api/orders/status/" + status)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].status").value("APPROVED"));
        }
    }

    @Test
    @DisplayName("4. Deve retornar uma página de pedidos filtrada por período cronológico")
    void shouldReturnPaginatedOrdersByPeriod() throws Exception {
        var start = "2026-06-01T00:00:00";
        var end = "2026-06-30T23:59:59";
        var mockDomainObject = mock(OrderDTO.class);
        var pageable = PageRequest.of(0, 10);
        var servicePage = new PageImpl<>(List.of(mockDomainObject), pageable, 1);

        var expectedResponse = new OrderResponse(99L, 1L, Collections.emptyList(), new BigDecimal("1200.00"), OrderStatus.PENDING, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.getByPeriod(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(servicePage);

        try (MockedStatic<com.api.order.management.b2b.mapper.OrderMapper> mapperMock = mockStatic(com.api.order.management.b2b.mapper.OrderMapper.class)) {
            mapperMock.when(() -> fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(get("/api/orders/period")
                            .param("start", start)
                            .param("end", end)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].id").value(99L));
        }
    }

    @Test
    @DisplayName("5. Deve retornar 200 Ok ao atualizar o status do pedido")
    void shouldReturn200WhenUpdatingStatus() throws Exception {
        var orderId = 1L;
        var requestDto = new StatusUpdateRequest(OrderStatus.APPROVED);
        var mockDomainObject = mock(OrderDTO.class);
        var expectedResponse = new OrderResponse(orderId, 1L, Collections.emptyList(), new BigDecimal("250.00"), OrderStatus.APPROVED, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.updateStatus(orderId, OrderStatus.APPROVED)).thenReturn(mockDomainObject);

        try (MockedStatic<com.api.order.management.b2b.mapper.OrderMapper> mapperMock = mockStatic(com.api.order.management.b2b.mapper.OrderMapper.class)) {
            mapperMock.when(() -> fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(patch("/api/orders/" + orderId + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("APPROVED"));
        }
    }

    @Test
    @DisplayName("6. Deve retornar 200 Ok ao cancelar um pedido")
    void shouldReturn200WhenCancellingOrder() throws Exception {
        var orderId = 1L;
        var mockDomainObject = mock(OrderDTO.class);
        var expectedResponse = new OrderResponse(orderId, 1L, Collections.emptyList(), new BigDecimal("0.00"), OrderStatus.CANCELED, LocalDateTime.now(), LocalDateTime.now());

        when(orderService.cancelOrder(orderId)).thenReturn(mockDomainObject);

        try (MockedStatic<com.api.order.management.b2b.mapper.OrderMapper> mapperMock = mockStatic(com.api.order.management.b2b.mapper.OrderMapper.class)) {
            mapperMock.when(() -> fromDTOToResponse(any())).thenReturn(expectedResponse);

            mockMvc.perform(post("/api/orders/" + orderId + "/cancel"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELED"));
        }
    }
}
