package com.api.order.management.b2b.db;

import com.api.order.management.b2b.db.repository.OrderRepository;
import com.api.order.management.b2b.dto.OrderItemDTO;
import com.api.order.management.b2b.dto.PartnerDTO;
import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.exception.BusinessException;
import com.api.order.management.b2b.exception.DatabaseException;
import com.api.order.management.b2b.exception.ResourceNotFoundException;
import com.api.order.management.b2b.mapper.OrderItemMapper;
import com.api.order.management.b2b.model.OrderModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OrderDb {

    private static final Logger log = LoggerFactory.getLogger(OrderDb.class);

    private final OrderRepository orderRepository;

    @Transactional
    public OrderModel save(final List<OrderItemDTO> orderItemDTOList, final PartnerDTO partnerDTO, final BigDecimal totalValue) {
        try {
            log.info("Iniciando persistência de novo pedido. PartnerId: {}, Itens: {}, Total: R$ {}",
                    partnerDTO.getId(), orderItemDTOList.size(), totalValue);

            final var order = OrderModel.builder()
                    .partnerId(partnerDTO.getId())
                    .totalValue(totalValue)
                    .status(OrderStatus.PENDING)
                    .items(orderItemDTOList.stream().map(OrderItemMapper::fromDTOToModel).toList())
                    .build();

            final var savedOrder = orderRepository.save(order);
            log.info("Pedido persistido com sucesso. OrderId: {}, Status: {}", savedOrder.getId(), savedOrder.getStatus());
            return savedOrder;
        } catch (Exception e) {
            log.error("Falha ao persistir o pedido para o parceiro ID: {}. Erro: {}", partnerDTO.getId(), e.getMessage(), e);
            throw new DatabaseException("Erro interno ao salvar o pedido no banco de dados.", e);
        }
    }

    @Transactional
    public OrderModel update(final OrderModel orderModel) {
        try {
            log.info("Iniciando atualização do pedido ID: {}. Novo Status: {}", orderModel.getId(), orderModel.getStatus());

            final var updatedOrder = orderRepository.save(orderModel);
            log.info("Pedido ID: {} atualizado com sucesso no banco de dados.", updatedOrder.getId());
            return updatedOrder;
        } catch (Exception e) {
            log.error("Falha ao atualizar o pedido ID: {}. Erro: {}", orderModel.getId(), e.getMessage(), e);
            throw new DatabaseException("Erro interno ao atualizar dados do pedido.", e);
        }
    }

    @Transactional(readOnly = true)
    public OrderModel findByIdWithItems(final Long orderId) {
        try {
            log.info("Buscando pedido com itens por ID: {}", orderId);

            return orderRepository.findByIdWithItems(orderId)
                    .orElseThrow(() -> {
                        log.warn("Pedido ID: {} com itens não localizado no banco de dados.", orderId);
                        return new ResourceNotFoundException("Pedido não encontrado");
                    });
        } catch (BusinessException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erro inesperado ao consultar pedido ID: {}. Erro: {}", orderId, e.getMessage(), e);
            throw new DatabaseException("Erro ao processar consulta de pedido com itens.", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderModel> findByStatus(final OrderStatus status, final Pageable pageable) {
        try {
            log.info("Consultando pedidos por status: {}, Página: {}, Tamanho: {}", status, pageable.getPageNumber(), pageable.getPageSize());

            return orderRepository.findByStatus(status, pageable);
        } catch (Exception e) {
            log.error("Erro ao listar pedidos por status ({}). Erro: {}", status, e.getMessage(), e);
            throw new DatabaseException("Erro ao processar listagem de pedidos por status.", e);
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderModel> findByCreatedAtBetween(final LocalDateTime start, final LocalDateTime end, final Pageable pageable) {
        try {
            log.info("Consultando pedidos por período: {} até {}, Página: {}", start, end, pageable.getPageNumber());

            return orderRepository.findByCreatedAtBetween(start, end, pageable);
        } catch (Exception e) {
            log.error("Erro ao listar pedidos por período ({} a {}). Erro: {}", start, end, e.getMessage(), e);
            throw new DatabaseException("Erro ao processar listagem de pedidos por período.", e);
        }
    }
}
