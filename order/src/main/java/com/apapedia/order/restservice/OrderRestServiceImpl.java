package com.apapedia.order.restservice;

import com.apapedia.order.dto.OrderMapper;
import org.springframework.security.core.AuthenticationException;
import com.apapedia.order.dto.request.ChangeOrderStatusRequestDTO;
import com.apapedia.order.dto.request.CreateOrderRequestDTO;
import com.apapedia.order.model.Order;
import com.apapedia.order.model.OrderItem;
import com.apapedia.order.repository.OrderDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderRestServiceImpl implements OrderRestService {

    OrderDb orderDb;

    OrderMapper orderMapper;

    private Order saveOrder(Order order) {
        return orderDb.save(order);
    }

    @Override
    public Order createOrder(CreateOrderRequestDTO createOrderRequestDTO) {
        Order order = orderMapper.createOrderRequestDTOToOrder(createOrderRequestDTO);
        return this.saveOrder(order);
    }

    @Override
    public Order changeOrderStatus(ChangeOrderStatusRequestDTO changeOrderStatusRequestDTO, String tokenUserId) {
        Order order = orderDb.findById(changeOrderStatusRequestDTO.getId()).orElse(null);
        if (order != null) {
            if (!order.getCustomer().toString().equals(tokenUserId)
                    && !order.getSeller().toString().equals(tokenUserId)) {
                throw new AuthenticationException("User ID does not match") {};
            }
            order.setStatus(changeOrderStatusRequestDTO.getStatus());
            return this.saveOrder(order);
        } else {
            throw new IllegalArgumentException("Order ID not found");
        }
    }

    @Override
    public List<Order> getOrderByCustomerId(UUID customerId) {
        return orderDb.findByCustomer(customerId);
    }

    @Override
    public List<Order> getOrderBySellerId(UUID sellerId) {
        return orderDb.findBySeller(sellerId);
    }

    @Override
    public Map<Integer, Integer> getSalesPerDay(UUID sellerId) {
        List<Order> sellerOrders = orderDb.findBySeller(sellerId);
        if (sellerOrders.isEmpty()) {
            return null;
        }
        LocalDate today = LocalDate.now();
        int currentDate = today.getDayOfMonth();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        // Create a map to store the count for each day
        Map<Integer, Integer> itemCountPerDay = new HashMap<>();
        for (int i = 1; i <= currentDate; i++) {
            itemCountPerDay.put(i, 0);
        }
        // Iterate over each order
        for (Order order : sellerOrders) {
            // Extract the day from the createdAt attribute of the order
            LocalDate localDate = order.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonthValue();
            int year = localDate.getYear();
            if (year == currentYear && month == currentMonth && day <= currentDate) {
                int itemCount = 0;
                for (OrderItem orderItem : order.getListOrderItem()) {
                    itemCount += orderItem.getQuantity();
                }

                // Update the count in the map
                itemCountPerDay.merge(day, itemCount, Integer::sum);
            }
        }

        for (Integer count : itemCountPerDay.values()) {
            if (count != 0)
                return itemCountPerDay;
        }

        return null;
    }

}
