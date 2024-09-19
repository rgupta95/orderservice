package com.sdet.idea.orderservice.repository;

import com.sdet.idea.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
