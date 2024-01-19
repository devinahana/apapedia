package com.apapedia.order.restservice;

import com.apapedia.order.dto.request.ChangeOrderStatusRequestDTO;
import com.apapedia.order.model.Order;

import java.util.List;
import java.util.Map;
import java.util.UUID;


public interface OrderRestService {
    Order saveOrder(Order order);

    Order changeOrderStatus(ChangeOrderStatusRequestDTO changeOrderStatusRequestDTO);

    List<Order> getOrderByCustomerId(UUID customerId);

    List<Order> getOrderBySellerId(UUID sellerId);

    Map<Integer, Integer> getSalesPerDay(UUID sellerId);
}
