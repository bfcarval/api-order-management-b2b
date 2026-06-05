package com.api.order.management.b2b.controller.interfaces;

import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.controller.request.StatusUpdateRequest;
import com.api.order.management.b2b.controller.response.OrderResponse;
import com.api.order.management.b2b.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@RequestMapping("/api/orders")
@Tag(name = "B2B Orders API", description = "Endpoints de gerenciamento de pedidos e créditos")
public interface OrderControllerAPI {

    @PostMapping
    @Operation(summary = "Criar um novo pedido de parceiro")
    ResponseEntity<OrderResponse> create(
            @RequestHeader(value = "x-idempotency-key") String idempotencyKey,
            @RequestBody @Valid OrderRequest orderRequest
    );

    @GetMapping("/{id}")
    @Operation(summary = "Consultar pedido por ID")
    ResponseEntity<OrderResponse> getById(@PathVariable Long id);

    @GetMapping("/status/{status}")
    @Operation(summary = "Consultar pedidos filtrando por status com paginação")
    ResponseEntity<Page<OrderResponse>> getByStatus(
            @PathVariable OrderStatus status,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable);

    @GetMapping("/period")
    @Operation(summary = "Consultar pedidos por período de criação com paginação")
    ResponseEntity<Page<OrderResponse>> getByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable);

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar o status de um pedido (Garante débito de crédito se aprovado)")
    ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusUpdateRequest statusUpdateRequest);

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar um pedido (Garante estorno de crédito se aplicável)")
    ResponseEntity<OrderResponse> cancel(@PathVariable Long id);
}
