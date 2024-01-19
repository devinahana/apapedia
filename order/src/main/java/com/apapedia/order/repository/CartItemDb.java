package com.apapedia.order.repository;

import com.apapedia.order.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartItemDb extends JpaRepository<CartItem, UUID> {
}
