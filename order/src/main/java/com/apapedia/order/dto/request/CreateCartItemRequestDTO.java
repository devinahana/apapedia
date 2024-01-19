package com.apapedia.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartItemRequestDTO {
    @NotNull(message = "Product id cannot be empty")
    private UUID productId;

    @NotNull(message = "Cart id cannot be empty")
    private UUID cartId;

    @NotNull(message = "Product price cannot be empty")
    private BigInteger productPrice;

    @NotNull(message = "Product quantity cannot be empty")
    private Integer quantity;

}
