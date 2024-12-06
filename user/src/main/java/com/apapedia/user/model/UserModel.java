package com.apapedia.user.model;

import com.apapedia.user.model.enumerator.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Table(name="user_model")
@JsonIgnoreProperties(value = {"password"}, allowSetters = true)
public class UserModel {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    @Size(max = 50)
    private String name;

    @NotNull
    @Column(unique = true)
    private String username;

    @NotNull
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private BigDecimal balance = BigDecimal.ZERO;

    private String address;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull
    private boolean isDeleted = false;

}

