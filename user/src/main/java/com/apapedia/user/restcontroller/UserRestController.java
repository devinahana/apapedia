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
import com.apapedia.user.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/user")
public class UserRestController {
    UserService userService;
    UserMapper userMapper;
    ResponseUtils responseUtils;

    @Operation(summary = "Get User by ID", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseUserModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Invalid user ID format. It should be a valid UUID.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cannot set user authentication: JWT String argument cannot be null or empty.\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to fetch this user data\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"User not found.\"}")))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id, @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID userIdUUID = UUID.fromString(id);
            if (!id.equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("fetch this user data");
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

    @Operation(summary = "Register User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseUserModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Username already exist\"}"))),
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody CreateUserRequestDTO requestDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
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

    @Operation(summary = "Login User")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseLogin.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Wrong password\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Username not found\"}")))
    })
    @PostMapping(value = "/login")
    public ResponseEntity<?> loginUser(
            @Valid @RequestBody LoginRequestDTO requestDTO,
            BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
            }

            LoginResponseDTO user = userService.login(requestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "User logged in successfully", user));
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
                    .body(new BaseResponse<>(false, "Failed login user : " + e.getMessage()));
        }
    }

    @Operation(summary = "Delete User by ID", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": true, \"message\": \"User deleted successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Invalid user ID format. It should be a valid UUID.\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cannot set user authentication: JWT String argument cannot be null or empty.\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to delete this user\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"User not found\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable String id, @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID userIdUUID = UUID.fromString(id);
            if (!id.equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("delete this user");
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

    @Operation(summary = "Update User Balance", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseUserModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Amount cannot be empty\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cannot set user authentication: JWT String argument cannot be null or empty.\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to update balance of this user\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"User not found\"}")))
    })
    @PutMapping("/balance")
    public ResponseEntity<?> updateUserBalance(
            @Valid @RequestBody UpdateBalanceRequestDTO requestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
            }

            if (!requestDTO.getId().toString().equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("update balance of this user");
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

    @Operation(summary = "Update User Data", parameters = @Parameter(in = ParameterIn.HEADER, name = "Authorization", description = "Bearer token for authentication", required = true, schema = @Schema(type = "string")))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful Response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseResponseUserModel.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Email should be valid\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"Cannot set user authentication: JWT String argument cannot be null or empty.\"}"))),
            @ApiResponse(responseCode = "403", description = "Invalid JWT Token", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"You are not allowed to update this user detail\"}"))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"isSuccess\": false, \"message\": \"User not found\"}")))
    })
    @PutMapping("")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
            }

            if (!updateUserRequestDTO.getId().toString().equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("update this user detail");
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
}

class BaseResponseUserModel extends BaseResponse<UserModel> {
}

class BaseResponseLogin extends BaseResponse<LoginResponseDTO> {
}
