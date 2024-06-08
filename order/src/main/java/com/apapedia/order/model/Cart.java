package com.apapedia.order.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart {

    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Column(unique = true)
    private UUID userId;

    @NotNull
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CartItem> listCartItem = new ArrayList<>();
}
