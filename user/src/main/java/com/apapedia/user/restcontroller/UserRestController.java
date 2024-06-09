package com.apapedia.user.restcontroller;

import com.apapedia.user.dto.UserMapper;
import com.apapedia.user.dto.request.CreateUserRequestDTO;
import com.apapedia.user.dto.request.LoginRequestDTO;
import com.apapedia.user.dto.request.UpdateBalanceRequestDTO;
import com.apapedia.user.dto.request.UpdateUserRequestDTO;
import com.apapedia.user.dto.response.BaseResponse;
import com.apapedia.user.dto.response.LoginResponseDTO;
import com.apapedia.user.model.UserModel;
import com.apapedia.user.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/user")
public class UserRestController {
    UserService userService;
    UserMapper userMapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id, @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID userIdUUID = UUID.fromString(id);
            if (!id.equals(tokenUserId)) {
                return getForbiddenResponse("fetch this user data");
            }
            UserModel user = userService.getUserById(userIdUUID);
            return ResponseEntity.ok(new BaseResponse<>(true, "User fetched successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid user ID format. It should be a valid UUID."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching user : " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserRequestDTO requestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(getBindingErrorMessage(bindingResult));
            }

            UserModel user = userService.createUser(requestDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new BaseResponse<>(true, "User created successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed creating user : " + e.getMessage()));
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO requestDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(getBindingErrorMessage(bindingResult));
            }

            LoginResponseDTO user = userService.login(requestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "User logged in successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed login user : " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id, @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID userIdUUID = UUID.fromString(id);
            if (!id.equals(tokenUserId)) {
                return getForbiddenResponse("delete this user");
            }

            userService.deleteUserById(userIdUUID);
            return ResponseEntity.ok(new BaseResponse<>(true, "User deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid user ID format. It should be a valid UUID."));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed deleting user : " + e.getMessage()));
        }
    }

    @PutMapping("/balance")
    public ResponseEntity<?> updateUserBalance(
            @Valid @RequestBody UpdateBalanceRequestDTO requestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(getBindingErrorMessage(bindingResult));
            }

            if (!requestDTO.getId().toString().equals(tokenUserId)) {
                return getForbiddenResponse("update balance of this user");
            }

            UserModel user = userService.updateUserBalance(requestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "User balance updated successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed updating user balance : " + e.getMessage()));
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(getBindingErrorMessage(bindingResult));
            }

            if (!updateUserRequestDTO.getId().toString().equals(tokenUserId)) {
                return getForbiddenResponse("update this user detail");
            }

            UserModel user = userService.updateUser(updateUserRequestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "User detail updated successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed updating user detail : " + e.getMessage()));
        }
    }

    private String getBindingErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessages = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors ) {
            errorMessages
                    .append(error.getDefaultMessage())
                    .append("\n");
        }
        return errorMessages.toString();
    }

    private ResponseEntity<?> getForbiddenResponse(String action) {
        return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new BaseResponse<>(false, "You are not allowed to " + action));
    }
}

