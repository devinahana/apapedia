package com.apapedia.user.dto;

import org.mapstruct.Mapper;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.model.CustomerModel;
import com.apapedia.user.model.SellerModel;

@Mapper(componentModel = "spring")
public interface UserMapper {

    CustomerModel createUserRequestDTOToCustomerModel(CreateUserRequestDTO createUserRequestDTO);

    SellerModel createUserRequestDTOToSellerModel(CreateUserRequestDTO createUserRequestDTO);

}
