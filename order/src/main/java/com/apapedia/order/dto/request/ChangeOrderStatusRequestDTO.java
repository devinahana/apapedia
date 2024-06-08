package com.apapedia.order.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeOrderStatusRequestDTO {
    @NotNull(message = "Order id cannot be empty")
    private UUID id;

    @NotNull(message = "Status cannot be empty")
    @Min(value = 0, message = "Status must be in the range 0-5")
    @Max(value = 5, message = "Status must be in the range 0-5")
    private Integer status;
}
