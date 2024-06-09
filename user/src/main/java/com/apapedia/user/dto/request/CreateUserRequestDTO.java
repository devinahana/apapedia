package com.apapedia.user.dto.request;

import com.apapedia.user.model.enumerator.Role;
import com.apapedia.user.model.enumerator.SellerCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequestDTO {

    @NotNull(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Username cannot be empty")
    private String username;

    @NotNull(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password cannot be empty")
    private String password;

    private String address;

    @NotNull(message = "Role cannot be empty")
    private Role role;

    private SellerCategory category;

}
