package com.apapedia.order.repository;

import com.apapedia.order.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface CartDb extends JpaRepository<Cart, UUID> {
    Cart findByUserId(UUID userID);
}
