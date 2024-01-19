package com.apapedia.order.dto.request;

import com.apapedia.order.model.OrderItem;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequestDTO {

    @NotNull(message = "Customer id cannot be empty")
    private UUID customer;

    @NotNull(message = "Seller id cannot be empty")
    private UUID seller;

    @NotEmpty(message = "List of order items cannot be empty")
    private List<OrderItem> listOrderItem;
}
