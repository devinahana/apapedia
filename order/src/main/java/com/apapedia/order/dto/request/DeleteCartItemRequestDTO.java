package com.apapedia.order.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteCartItemRequestDTO {
    @NotNull(message = "Cart ID cannot be empty")
    private UUID cartId;

    @NotNull(message = "Cart item ID cannot be empty")
    private UUID cartItemId;
}
