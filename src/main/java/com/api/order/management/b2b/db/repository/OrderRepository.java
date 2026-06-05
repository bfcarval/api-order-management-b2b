package com.api.order.management.b2b.db.repository;

import com.api.order.management.b2b.enums.OrderStatus;
import com.api.order.management.b2b.model.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    @Query("SELECT o FROM OrderModel o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<OrderModel> findByIdWithItems(final Long id);

    @Query(value = "SELECT o FROM OrderModel o WHERE o.status = :status",
            countQuery = "SELECT count(o) FROM OrderModel o WHERE o.status = :status")
    Page<OrderModel> findByStatus(final OrderStatus status, final Pageable pageable);

    @Query(value = "SELECT o FROM OrderModel o WHERE o.createdAt BETWEEN :start AND :end",
            countQuery = "SELECT count(o) FROM OrderModel o WHERE o.createdAt BETWEEN :start AND :end")
    Page<OrderModel> findByCreatedAtBetween(final LocalDateTime start, final LocalDateTime end, final Pageable pageable);
}
