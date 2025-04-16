package com.example.demo.service;


import com.example.demo.DTO.OrderDTO;
import com.example.demo.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {

    Order createOrder(Long userId, OrderDTO orderDTO);

    Optional<Order> getOrderById(Long id);

    Page<Order> getOrdersByUserId(Long userId, Pageable pageable);

    List<Order> getOrdersByUserIdSorted(Long userId);

    Page<Order> getOrdersByStatus(String status, Pageable pageable);

    List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    Long countOrdersByStatus(String status);

    Double calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate);

    List<Order> getActiveOrdersByUserId(Long userId);

    Order updateOrderStatus(Long id, String status);

    Order updatePaymentStatus(Long id, String paymentStatus);
}