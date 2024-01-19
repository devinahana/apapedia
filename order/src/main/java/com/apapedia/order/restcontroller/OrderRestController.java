package com.apapedia.order.restcontroller;

import com.apapedia.order.dto.OrderMapper;
import com.apapedia.order.dto.request.ChangeOrderStatusRequestDTO;
import com.apapedia.order.dto.request.CreateOrderRequestDTO;
import com.apapedia.order.model.Order;
import com.apapedia.order.restservice.OrderRestService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/api")
public class OrderRestController {
    OrderRestService orderService;

    OrderMapper orderMapper;

    // POST : Create new order
    @PostMapping("/create-order")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody CreateOrderRequestDTO createOrderRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }
        Order order = orderMapper.createOrderRequestDTOToOrder(createOrderRequestDTO);
        order = orderService.saveOrder(order);

        return ResponseEntity.ok(order);
    }

    // PUT : Change order status
    @PutMapping("/change-order-status")
    public ResponseEntity<Order> changeOrderStatus(@Valid @RequestBody ChangeOrderStatusRequestDTO changeOrderStatusRequestDTO, BindingResult bindingResult) {
        if (bindingResult.hasFieldErrors()) {
            String errorMessages = "";
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors ) {
                errorMessages += error.getField() + " - " + error.getDefaultMessage() + "\n";
            }
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    errorMessages
            );
        }

        Order order = orderService.changeOrderStatus(changeOrderStatusRequestDTO);
        if (order == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order is not found"
            );
        }
        return ResponseEntity.ok(order);
    }

    // GET Order by Customer Id
    @GetMapping("/get-order/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrderByCustomerId(@PathVariable("customerId") String customerId) {
        List<Order> listOrders = orderService.getOrderByCustomerId(UUID.fromString(customerId));
        if (listOrders == null || listOrders.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Customer does not have any orders"
            );
        }

        return ResponseEntity.ok(listOrders);
    }

    // GET Order by Seller Id
    @GetMapping("/get-order/seller/{sellerId}")
    public ResponseEntity<List<Order>> getOrderBySellerId(@PathVariable("sellerId") String sellerId) {
        List<Order> listOrders = orderService.getOrderBySellerId(UUID.fromString(sellerId));
        if (listOrders == null || listOrders.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Seller does not have any orders"
            );
        }

        return ResponseEntity.ok(listOrders);
    }

    // GET sales per day for the month
    @GetMapping("/sales-per-day/{sellerId}")
    public ResponseEntity<Map<Integer, Integer>> getSalesPerDay(@PathVariable("sellerId") String sellerId) {
        Map<Integer, Integer> soldPerDay = orderService.getSalesPerDay(UUID.fromString(sellerId));
        if (soldPerDay == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No items have been sold this month"
            );
        }
        return ResponseEntity.ok(soldPerDay);
    }

}
