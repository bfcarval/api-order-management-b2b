package com.api.order.management.b2b.service;

import com.api.order.management.b2b.controller.request.OrderItemRequest;
import com.api.order.management.b2b.controller.request.OrderRequest;
import com.api.order.management.b2b.db.OrderDb;
import com.api.order.management.b2b.db.PartnerDb;
import com.api.order.management.b2b.dto.OrderDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.exception.BusinessException;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.api.order.management.b2b.enums.OrderStatus.CANCELED;
import static com.api.order.management.b2b.mapper.OrderItemMapper.mapItemsFromRequest;
import static com.api.order.management.b2b.mapper.OrderMapper.fromModelToDTO;
import static com.api.order.management.b2b.mapper.PartnerMapper.fromModelToDTO;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderDb orderDb;
    private final PartnerDb partnerDb;
    private final NotificationService notificationService;

    private final ConcurrentHashMap<String, Boolean> idempotencyCache = new ConcurrentHashMap<>();

    public OrderDTO createOrder(final String idempotencyKey, final OrderRequest request) {
        if (Boolean.TRUE.equals(idempotencyCache.putIfAbsent(idempotencyKey, true))) {
            log.warn("Requisição duplicada bloqueada pela chave de idempotência: {}", idempotencyKey);
            throw new BusinessException("Esta transação já foi processada ou está em andamento.");
        }

        log.info("Processando criação de novo pedido para o parceiro ID: {}", request.partnerId());

        try {
            final var partnerModel = fromModelToDTO(partnerDb.findById(request.partnerId()));
            final var totalValue = calculateTotalValue(request.items());

            if (partnerModel.getCreditLimit().compareTo(totalValue) < 0) {
                log.warn("Crédito negado para parceiro ID: {}. Saldo atual: R$ {}, Total do pedido: R$ {}",
                        request.partnerId(), partnerModel.getCreditLimit(), totalValue);
                throw new BusinessException("Limite de crédito insuficiente para esta operação B2B.");
            }

            final var items = mapItemsFromRequest(request);
            final var savedOrderDTO = fromModelToDTO(orderDb.save(items, partnerModel, totalValue));

            log.info("Pedido ID: {} criado com sucesso para o parceiro ID: {}", savedOrderDTO.getId(), request.partnerId());
            notificationService.sendStatusNotification(savedOrderDTO);

            return savedOrderDTO;
        } catch (BusinessException | ResourceNotFoundException | DatabaseException e) {
            idempotencyCache.remove(idempotencyKey);
            throw e;
        } catch (Exception e) {
            idempotencyCache.remove(idempotencyKey);
            log.error("Erro inesperado ao criar pedido para parceiro ID: {}. Motivo: {}", request.partnerId(), e.getMessage(), e);
            throw new BusinessException("Falha ao processar as regras de negócio para criação do pedido.", e);
        }
    }


    public OrderDTO updateStatus(final Long orderId, final OrderStatus newStatus) {
        log.info("Processando atualização de status do pedido ID: {} para {}", orderId, newStatus);

        try {
            final var orderModel = orderDb.findByIdWithItems(orderId);

            if (newStatus == CANCELED) {
                log.info("Pedido {} deve ser cancelado na rota de cancelamento.", orderModel.getId());
                throw new BusinessException("Pedido ".concat(orderModel.getId().toString()).concat(" deve ser cancelado em sua respectiva rota."));
            }

            if (orderModel.getStatus() == newStatus) {
                log.info("Pedido {} já possui o status {}. Nenhuma alteração realizada.", orderModel.getId(), newStatus);
                throw new BusinessException("Pedido ".concat(orderModel.getId().toString()).concat(" já possui o status requerido."));
            }

            if (newStatus == OrderStatus.APPROVED && orderModel.getStatus() == OrderStatus.PENDING) {
                log.info("Pedido ID: {} aprovado. Iniciando retenção de limite de crédito do parceiro ID: {}", orderId, orderModel.getPartnerId());
                final var partnerModel = partnerDb.findById(orderModel.getPartnerId());

                if (partnerModel.getCreditLimit().compareTo(orderModel.getTotalValue()) < 0) {
                    log.warn("Falha na aprovação do pedido ID: {}. Crédito insuficiente para o parceiro ID: {}", orderId, partnerModel.getId());
                    throw new BusinessException("Saldo de crédito insuficiente para aprovação.");
                }

                partnerModel.setCreditLimit(partnerModel.getCreditLimit().subtract(orderModel.getTotalValue()));
                partnerDb.update(partnerModel);
                log.info("Crédito debitado com sucesso do parceiro ID: {}. Novo limite: R$ {}", partnerModel.getId(), partnerModel.getCreditLimit());
            }

            orderModel.setStatus(newStatus);
            final var updatedOrderDTO = fromModelToDTO(orderDb.update(orderModel));

            log.info("Status do pedido ID: {} alterado com sucesso para {}", orderId, newStatus);
            notificationService.sendStatusNotification(updatedOrderDTO);

            return updatedOrderDTO;
        } catch (BusinessException | ResourceNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar status do pedido ID: {}. Motivo: {}", orderId, e.getMessage(), e);
            throw new BusinessException("Falha ao processar alteração de status do pedido.");
        }
    }

    public OrderDTO cancelOrder(final Long orderId) {
        log.info("Processando cancelamento do pedido ID: {}", orderId);

        try {
            final var orderModel = orderDb.findByIdWithItems(orderId);

            if (orderModel.getStatus() == CANCELED) {
                log.info("Pedido {} já cancelado.", orderModel.getId());
                throw new BusinessException("Pedido ".concat(orderModel.getId().toString()).concat(" já cancelado."));
            }

            if (orderModel.getStatus() != OrderStatus.PENDING) {
                log.info("Pedido ID: {} cancelado após aprovação. Iniciando estorno de R$ {} para o parceiro ID: {}",
                        orderId, orderModel.getTotalValue(), orderModel.getPartnerId());

                final var partnerModel = partnerDb.findById(orderModel.getPartnerId());
                partnerModel.setCreditLimit(partnerModel.getCreditLimit().add(orderModel.getTotalValue()));
                partnerDb.update(partnerModel);
                log.info("Crédito estornado com sucesso para o parceiro ID: {}. Novo limite: R$ {}", partnerModel.getId(), partnerModel.getCreditLimit());
            }

            orderModel.setStatus(OrderStatus.CANCELED);
            final var cancelledOrderDTO = fromModelToDTO(orderDb.update(orderModel));

            log.info("Pedido ID: {} cancelado com sucesso no sistema.", orderId);
            notificationService.sendStatusNotification(cancelledOrderDTO);

            return cancelledOrderDTO;
        } catch (BusinessException | ResourceNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao cancelar o pedido ID: {}. Motivo: {}", orderId, e.getMessage(), e);
            throw new BusinessException("Falha ao processar cancelamento do pedido.");
        }
    }

    public OrderDTO getById(final Long id) {
        log.info("Processando busca detalhada do pedido ID: {}", id);

        try {
            return fromModelToDTO(orderDb.findByIdWithItems(id));
        } catch (ResourceNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao buscar pedido ID: {}. Motivo: {}", id, e.getMessage(), e);
            throw new BusinessException("Falha ao processar consulta de pedido.");
        }
    }

    public Page<OrderDTO> getByStatus(final OrderStatus status, final Pageable pageable) {
        log.info("Processando busca paginada de pedidos por status: {}", status);

        try {
            return orderDb.findByStatus(status, pageable).map(OrderMapper::fromModelToDTO);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao listar pedidos por status ({}). Motivo: {}", status, e.getMessage(), e);
            throw new BusinessException("Falha ao buscar pedidos por status.");
        }
    }

    public Page<OrderDTO> getByPeriod(final LocalDateTime start, final LocalDateTime end, final Pageable pageable) {
        log.info("Processando busca paginada de pedidos por período: {} até {}", start, end);

        try {
            return orderDb.findByCreatedAtBetween(start, end, pageable).map(OrderMapper::fromModelToDTO);
        } catch (DatabaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao listar pedidos por período. Motivo: {}", e.getMessage(), e);
            throw new BusinessException("Falha ao buscar pedidos por período.");
        }
    }

    private BigDecimal calculateTotalValue(final List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
