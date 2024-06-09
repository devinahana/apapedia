package com.apapedia.user.model;

import lombok.*;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Data
@Table(name = "customer")
public class CustomerModel extends UserModel {
    @NotNull
    @Column(unique = true)
    private UUID cart;
}

