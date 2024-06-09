package com.apapedia.user.service;


import java.util.UUID;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.dto.request.LoginRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.dto.request.UpdateUserRequestDTO;
import com.apapedia.user.dto.response.LoginResponseDTO;
import com.apapedia.user.model.UserModel;


public interface UserService {


    UserModel getUserById(UUID id);

    UserModel createUser(CreateUserRequestDTO createUserRequestDTO);

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

    void deleteUserById(UUID id);

    UserModel updateUserBalance(UpdateBalanceRequestDTO updateBalanceRequestDTO);

    UserModel updateUser(UpdateUserRequestDTO updateUserRequestDTO);
}
