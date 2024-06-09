package com.apapedia.user.service;

import com.apapedia.user.dto.UserMapper;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.dto.request.LoginRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.dto.request.UpdateUserRequestDTO;
import com.apapedia.user.dto.response.LoginResponseDTO;
import com.apapedia.user.model.CustomerModel;
import com.apapedia.user.model.SellerModel;
import com.apapedia.user.model.UserModel;
import com.apapedia.user.repository.UserDb;
import com.apapedia.user.security.jwt.JwtUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import static com.apapedia.user.model.enumerator.Role.CUSTOMER;
import static com.apapedia.user.model.enumerator.Role.SELLER;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserDb userDb;

    private UserMapper userMapper;

    private JwtUtils jwtUtils;

    private PasswordEncoder passwordEncoder;

    private UserModel findById(UUID id) {
        UserModel user = userDb.findByIdAndIsDeletedFalse(id);
        if (user == null) {
            throw new NoSuchElementException("User with ID " + id + " not found");
        }
        return user;
    }

    private UserModel saveUser(UserModel userModel) {
        return userDb.save(userModel);
    }

    private boolean isUsernameExist(String username) {
        return userDb.findByUsernameIgnoreCase(username) != null;
    }

    private boolean isEmailExist(String email) {
        return userDb.findByEmailIgnoreCase(email) != null;
    }

    private String encrypt(String Password) {
        return passwordEncoder.encode(Password);
    }

    private UUID getCartId(UUID userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            Map<String, Object> requestBody = Map.of("userId", userId.toString());

            WebClient orderService = WebClient.builder()
                    .baseUrl("http://localhost:8083")
                    .build();
            String response = orderService
                    .post()
                    .uri("/api/cart")
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // blocking to get the result synchronously

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseJson = objectMapper.readTree(response);
            String cartId = responseJson.get("content").get("id").asText();
            return UUID.fromString(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Failed creating user cart : " + e.getMessage());
        }
    }

    @Override
    public UserModel getUserById(UUID id) {
        return this.findById(id);
    }

    @Override
    public UserModel createUser(CreateUserRequestDTO createUserRequestDTO) {
        if (this.isUsernameExist(createUserRequestDTO.getUsername())) {
            throw new IllegalArgumentException("Username is already used");
        } else if (this.isEmailExist(createUserRequestDTO.getEmail())) {
            throw new IllegalArgumentException("Email is already used");
        }

        String password = createUserRequestDTO.getPassword();
        createUserRequestDTO.setPassword(this.encrypt(password));

        switch (createUserRequestDTO.getRole()) {
            case CUSTOMER:
                CustomerModel customer = userMapper.createUserRequestDTOToCustomerModel(createUserRequestDTO);
                customer.setCart(this.getCartId(customer.getId()));
                return this.saveUser(customer);
            case SELLER:
                if (createUserRequestDTO.getCategory() == null) {
                    throw new IllegalArgumentException("Seller category cannot be empty");
                }
                SellerModel seller = userMapper.createUserRequestDTOToSellerModel(createUserRequestDTO);
                return this.saveUser(seller);
            default:
                throw new IllegalArgumentException("Invalid role");
        }
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();
        UserModel user = userDb.findByUsernameIgnoreCaseAndIsDeletedFalse(username);

        if (user == null) {
            throw new IllegalArgumentException("Username not found");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Wrong password");
        }

        return new LoginResponseDTO(
                jwtUtils.generateJwtToken(user.getId().toString(), username, user.getRole().toString()),
                user
        );
    }

    @Override
    public void deleteUserById(UUID id) {
        UserModel user = this.findById(id);
        user.setDeleted(true);
        this.saveUser(user);
    }

    @Override
    public UserModel updateUserBalance(UpdateBalanceRequestDTO updateBalanceRequestDTO) {
        UUID userId = updateBalanceRequestDTO.getId();
        UserModel user = this.findById(userId);

        if (updateBalanceRequestDTO.getPositive() == null) {
            user.setBalance(updateBalanceRequestDTO.getAmount());
        } else if (updateBalanceRequestDTO.getPositive()) {
            user.setBalance(
                    user.getBalance().add(updateBalanceRequestDTO.getAmount())
            );
        } else {
            user.setBalance(
                    user.getBalance().subtract(updateBalanceRequestDTO.getAmount())
            );
        }

        return this.saveUser(user);
    }

    @Override
    public UserModel updateUser(UpdateUserRequestDTO updateUserRequestDTO) {
        UUID userId = updateUserRequestDTO.getId();
        UserModel user = this.findById(userId);

        if (updateUserRequestDTO.getName() != null) {
            user.setName(updateUserRequestDTO.getName());
        }

        String email = updateUserRequestDTO.getEmail();
        if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
            if (this.isEmailExist(email)) {
                throw new IllegalArgumentException("Email is already used");
            }
            user.setEmail(email);
        }

        String password = updateUserRequestDTO.getPassword();
        if (password != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("New password cannot be identical with old password");
            }
            user.setPassword(this.encrypt(password));
        }

        if (updateUserRequestDTO.getAddress() != null) {
            user.setAddress(updateUserRequestDTO.getAddress());
        }

        return this.saveUser(user);
    }

}




