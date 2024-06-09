package com.apapedia.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequestDTO {
    @NotNull(message = "User ID cannot be empty")
    private UUID id;

    @NotNull(message = "Amount cannot be empty")
    private BigDecimal amount;

    private Boolean positive;
}
