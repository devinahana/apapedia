package com.apapedia.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserRequestDTO {
    @NotNull(message = "User ID cannot be empty")
    private UUID id;
    @Size(max = 50, message = "Name cannot be more than 50 characters")
    private String name;
    @Email(message = "Email should be valid")
    private String email;
    private String password;
    private String address;
}
