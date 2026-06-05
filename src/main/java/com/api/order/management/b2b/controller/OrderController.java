package com.api.order.management.b2b.controller;

import com.api.order.management.b2b.controller.interfaces.OrderControllerAPI;
import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.controller.request.StatusUpdateRequest;
import com.api.order.management.b2b.controller.response.OrderResponse;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.mapper.OrderMapper;
import com.api.order.management.b2b.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import static com.api.order.management.b2b.mapper.OrderMapper.fromDTOToResponse;

@RequiredArgsConstructor
@RestController
public class OrderController implements OrderControllerAPI {

    private final OrderService orderService;

    @Override
    public ResponseEntity<OrderResponse> create(final String idempotencyKey, final OrderRequest orderRequest) {
        return new ResponseEntity<>(
                fromDTOToResponse(orderService.createOrder(idempotencyKey, orderRequest)),
                HttpStatus.CREATED
        );
    }

    @Override
    public ResponseEntity<OrderResponse> getById(final Long id) {
        return ResponseEntity.ok(fromDTOToResponse(orderService.getById(id)));
    }

    @Override
    public ResponseEntity<Page<OrderResponse>> getByStatus(final OrderStatus status, final Pageable pageable) {
        return ResponseEntity.ok(orderService.getByStatus(status, pageable).map(OrderMapper::fromDTOToResponse));
    }

    @Override
    public ResponseEntity<Page<OrderResponse>> getByPeriod(final LocalDateTime start, final LocalDateTime end, final Pageable pageable) {
        return ResponseEntity.ok(orderService.getByPeriod(start, end, pageable).map(OrderMapper::fromDTOToResponse));
    }

    @Override
    public ResponseEntity<OrderResponse> updateStatus(final Long id, final StatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok(fromDTOToResponse(orderService.updateStatus(id, statusUpdateRequest.status())));
    }

    @Override
    public ResponseEntity<OrderResponse> cancel(Long id) {
        return ResponseEntity.ok(fromDTOToResponse(orderService.cancelOrder(id)));
    }
}
