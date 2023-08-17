package com.sendilkutay.orderservice.service;

import com.sendilkutay.orderservice.dto.InventoryResponse;
import com.sendilkutay.orderservice.dto.OrderLineItemsDto;
import com.sendilkutay.orderservice.dto.OrderRequest;
import com.sendilkutay.orderservice.model.Order;
import com.sendilkutay.orderservice.model.OrderLineItems;
import com.sendilkutay.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) throws IllegalAccessException {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        // Call inventory service and place order if product is in stock

        InventoryResponse[] inventoryResponses = webClient.get()
                .uri("http://localhost:8082/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean result = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::getIsInStock);

        if (Boolean.TRUE.equals(result)) {
            orderRepository.save(order);
        } else {
            throw new IllegalAccessException("Product is not in stock");
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderItem) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderItem.getPrice());
        orderLineItems.setQuantity(orderItem.getQuantity());
        orderLineItems.setSkuCode(orderItem.getSkuCode());
        return orderLineItems;
    }
}
