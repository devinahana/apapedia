package com.apapedia.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCartItemRequestDTO {
    @NotNull(message = "Product ID cannot be empty")
    private UUID productId;

    @NotNull(message = "Cart ID cannot be empty")
    private UUID cartId;

    @NotNull(message = "Product price cannot be empty")
    private BigDecimal productPrice;

    @NotNull(message = "Product quantity cannot be empty")
    @Min(value = 1, message = "Quantity cannot be less than 1")
    private Integer quantity;

}
