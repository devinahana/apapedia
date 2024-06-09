package com.apapedia.order.dto;

import com.apapedia.order.dto.request.CreateOrderRequestDTO;
import com.apapedia.order.model.Order;
import com.apapedia.order.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    Order createOrderRequestDTOToOrder(CreateOrderRequestDTO createOrderRequestDTO);

    @AfterMapping
    default void setOrderItem(@MappingTarget Order order, CreateOrderRequestDTO createOrderRequestDTO) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> listOrderItem = createOrderRequestDTO.getListOrderItem();
        if (listOrderItem != null && !listOrderItem.isEmpty()) {
            for (OrderItem orderItem : listOrderItem) {
                orderItem.setOrder(order);
                totalPrice = totalPrice.add(
                        orderItem.getProductPrice().multiply(
                                BigDecimal.valueOf(orderItem.getQuantity())
                        )
                );
            }
        }
        order.setTotalPrice(totalPrice);
    }

}
