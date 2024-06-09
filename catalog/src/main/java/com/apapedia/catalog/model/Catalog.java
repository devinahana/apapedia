package com.apapedia.catalog.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE catalog SET is_deleted = true WHERE id=?")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Catalog {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private UUID seller;

    @NotNull
    private BigDecimal price = BigDecimal.ZERO;

    @NotNull
    private String productName;

    @NotNull
    private String productDescription;

    @NotNull
    private int stock;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @JsonManagedReference
    private Category category;

    @NotNull
    private byte[] image;

    @NotNull
    private boolean isDeleted = false;

}
