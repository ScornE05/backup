package com.example.demo.Security;

import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("orderSecurity")
public class OrderSecurity {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderSecurity(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean isOwner(Long orderId, UserPrincipal userPrincipal) {
        Optional<Order> order = orderRepository.findById(orderId);

        if (order.isEmpty()) {
            return false;
        }

        return order.get().getUser().getId().equals(userPrincipal.getId());
    }
}
