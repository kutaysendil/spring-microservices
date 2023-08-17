package com.sendilkutay.orderservice.service;

import com.sendilkutay.orderservice.dto.OrderLineItemsDto;
import com.sendilkutay.orderservice.dto.OrderRequest;
import com.sendilkutay.orderservice.model.Order;
import com.sendilkutay.orderservice.model.OrderLineItems;
import com.sendilkutay.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        // Call inventory service and place order if product is in stock
        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderItem) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderItem.getPrice());
        orderLineItems.setQuantity(orderItem.getQuantity());
        orderLineItems.setSkuCode(orderItem.getSkuCode());
        return orderLineItems;
    }
}
