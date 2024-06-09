package com.apapedia.order.repository;

import com.apapedia.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderDb extends JpaRepository<Order, UUID> {
    List<Order> findByCustomer(UUID customerId);

    List<Order> findBySeller(UUID sellerId);
}
