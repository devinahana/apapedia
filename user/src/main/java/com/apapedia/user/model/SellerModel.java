package com.apapedia.user.model;

import com.apapedia.user.model.enumerator.SellerCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Data
@Table(name = "seller")
public class SellerModel extends UserModel {

    @NotNull
    @Enumerated(EnumType.STRING)
    private SellerCategory category;

}
