package com.apapedia.order.restcontroller;

import com.apapedia.order.dto.request.ChangeOrderStatusRequestDTO;
import com.apapedia.order.dto.request.CreateOrderRequestDTO;
import com.apapedia.order.dto.response.BaseResponse;
import com.apapedia.order.model.Order;
import com.apapedia.order.restservice.OrderRestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api/order/")
public class OrderRestController {
    OrderRestService orderService;


    // POST : Create new order
    @PostMapping("create")
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequestDTO createOrderRequestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, getBindingErrorMessage(bindingResult)));
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
    public ResponseEntity<?> changeOrderStatus(@Valid @RequestBody ChangeOrderStatusRequestDTO changeOrderStatusRequestDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasFieldErrors()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(false, getBindingErrorMessage(bindingResult)));
            }

            Order order = orderService.changeOrderStatus(changeOrderStatusRequestDTO);
            return ResponseEntity.ok(new BaseResponse<>(true, "Order status changed successfully", order));
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
    @GetMapping("customer/{customerId}")
    public ResponseEntity<?> getOrderByCustomerId(@PathVariable("customerId") String customerId) {
        try {
            UUID customerIdUUID = UUID.fromString(customerId);

            List<Order> listOrders = orderService.getOrderByCustomerId(customerIdUUID);
            if (listOrders == null || listOrders.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse<>(true, "Customer does not have any orders"));
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
    @GetMapping("seller/{sellerId}")
    public ResponseEntity<?> getOrderBySellerId(@PathVariable("sellerId") String sellerId) {
        try {
            UUID sellerIdUUID = UUID.fromString(sellerId);

            List<Order> listOrders = orderService.getOrderBySellerId(sellerIdUUID);
            if (listOrders == null || listOrders.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new BaseResponse<>(true, "Seller does not have any orders"));
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
    @GetMapping("monthly-sales/{sellerId}")
    public ResponseEntity<?> getSalesPerDay(@PathVariable("sellerId") String sellerId) {
        try {
            UUID sellerIdUUID = UUID.fromString(sellerId);

            Map<Integer, Integer> salesPerDay = orderService.getSalesPerDay(sellerIdUUID);
            if (salesPerDay == null) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(true, "No items have been sold this month"));
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

    private String getBindingErrorMessage(BindingResult bindingResult) {
        StringBuilder errorMessages = new StringBuilder();
        List<FieldError> errors = bindingResult.getFieldErrors();
        for (FieldError error : errors ) {
            errorMessages.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage())
                    .append("\n");
        }
        return errorMessages.toString();
    }

}
