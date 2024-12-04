package com.apapedia.order.restcontroller;

import com.apapedia.order.dto.request.ChangeOrderStatusRequestDTO;
import com.apapedia.order.dto.request.CreateOrderRequestDTO;
import com.apapedia.order.dto.response.BaseResponse;
import com.apapedia.order.model.Order;
import com.apapedia.order.restservice.OrderRestService;
import com.apapedia.order.utils.ResponseUtils;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/order")
public class OrderRestController {
    OrderRestService orderService;

    ResponseUtils responseUtils;

    // POST : Create new order
    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody CreateOrderRequestDTO createOrderRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, responseUtils.getBindingErrorMessage(bindingResult)));
            }

            if (!createOrderRequestDTO.getCustomer().toString().equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("create order for this user");
            }

            Order order = orderService.createOrder(createOrderRequestDTO);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new BaseResponse<>(true, "Order created successfully", order));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed creating order : " + e.getMessage()));
        }
    }

    // PUT : Change order status
    @PutMapping("/change-status")
    public ResponseEntity<?> changeOrderStatus(
            @Valid @RequestBody ChangeOrderStatusRequestDTO changeOrderStatusRequestDTO,
            BindingResult bindingResult,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            if (bindingResult.hasFieldErrors()) {
                throw new IllegalArgumentException(responseUtils.getBindingErrorMessage(bindingResult));
            }

            Order order = orderService.changeOrderStatus(changeOrderStatusRequestDTO, tokenUserId);
            return ResponseEntity.ok(new BaseResponse<>(true, "Order status changed successfully", order));
        } catch (AuthenticationException e) {
            return responseUtils.getForbiddenResponse("change order status for this user");
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed changing order status : " + e.getMessage()));
        }
    }

    // GET Order by Customer Id
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getOrderByCustomerId(
            @PathVariable String customerId,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID customerIdUUID = UUID.fromString(customerId);
            if (!customerId.equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("fetch order for this user");
            }

            List<Order> listOrders = orderService.getOrderByCustomerId(customerIdUUID);
            if (listOrders == null || listOrders.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse<>(true, "Customer does not have any orders"));
            }

            return ResponseEntity.ok(new BaseResponse<>(true, "Order fetched successfully", listOrders));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid customer ID format. It should be a valid UUID."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching order : " + e.getMessage()));
        }
    }

    // GET Order by Seller Id
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getOrderBySellerId(
            @PathVariable String sellerId,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID sellerIdUUID = UUID.fromString(sellerId);
            if (!sellerId.equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("fetch order for this user");
            }

            List<Order> listOrders = orderService.getOrderBySellerId(sellerIdUUID);
            if (listOrders == null || listOrders.isEmpty()) {
                return ResponseEntity.ok(new BaseResponse<>(true, "Seller does not have any orders"));
            }

            return ResponseEntity.ok(new BaseResponse<>(true, "Order fetched successfully", listOrders));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid seller ID format. It should be a valid UUID."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching order : " + e.getMessage()));
        }
    }

    // GET sales per day for this month
    @GetMapping("/monthly-sales/{sellerId}")
    public ResponseEntity<?> getSalesPerDay(
            @PathVariable String sellerId,
            @RequestAttribute("userId") String tokenUserId) {
        try {
            UUID sellerIdUUID = UUID.fromString(sellerId);
            if (!sellerId.equals(tokenUserId)) {
                return responseUtils.getForbiddenResponse("fetch monthly sales for this user");
            }

            Map<Integer, Integer> salesPerDay = orderService.getSalesPerDay(sellerIdUUID);
            if (salesPerDay == null) {
                return ResponseEntity.ok(new BaseResponse<>(true, "No items have been sold this month"));
            }

            return ResponseEntity.ok(new BaseResponse<>(true, "This month sales fetched successfully", salesPerDay));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(false, "Invalid seller ID format. It should be a valid UUID."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(false, "Failed fetching this month sales : " + e.getMessage()));
        }

    }

}
