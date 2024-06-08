package com.apapedia.order.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_item")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderItem {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private int quantity;

    @NotNull
    private UUID productId;

    @NotNull
    private String productName;

    @NotNull
    private BigDecimal productPrice = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @JsonBackReference
    private Order order;
}
