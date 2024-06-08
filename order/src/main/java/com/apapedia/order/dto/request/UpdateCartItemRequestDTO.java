package com.apapedia.order.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCartItemRequestDTO extends DeleteCartItemRequestDTO {

    @NotNull(message = "Product quantity cannot be empty")
    @Min(value = 1, message = "Quantity cannot be less than 1")
    private Integer quantity;

}
