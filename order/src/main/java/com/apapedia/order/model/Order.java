package com.apapedia.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    @Id
    private UUID id = UUID.randomUUID();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /*
    * 0: Menunggu konfirmasi
    * 1: Dikonfirmasi penjual
    * 2: Menunggu kurir
    * 3: Dalam perjalanan
    * 4: Barang diterima
    * 5: Selesai
    * */
    @NotNull
    @Min(0)
    @Max(5)
    private int status = 0;

    @NotNull
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @NotNull
    private UUID customer;

    @NotNull
    private UUID seller;

    @NotNull
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrderItem> listOrderItem;
}
