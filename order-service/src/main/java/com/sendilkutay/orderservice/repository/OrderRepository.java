package com.sendilkutay.orderservice.repository;

import com.sendilkutay.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository  extends JpaRepository<Order,Long> {
}
