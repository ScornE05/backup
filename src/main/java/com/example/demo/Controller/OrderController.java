package com.example.demo.Controller;


import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.OrderItemDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.Security.CurrentUser;
import com.example.demo.Security.UserPrincipal;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@CurrentUser UserPrincipal currentUser, @Valid @RequestBody OrderDTO orderDTO) {
        try {
            Order order = orderService.createOrder(currentUser.getId(), orderDTO);
            OrderDTO responseDTO = convertToDTO(order);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#id, principal)")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        OrderDTO orderDTO = convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/user")
    public ResponseEntity<Page<OrderDTO>> getCurrentUserOrders(
            @CurrentUser UserPrincipal currentUser,
            Pageable pageable) {
        Page<Order> orders = orderService.getOrdersByUserId(currentUser.getId(), pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/user/sorted")
    public ResponseEntity<List<OrderDTO>> getCurrentUserOrdersSorted(@CurrentUser UserPrincipal currentUser) {
        List<Order> orders = orderService.getOrdersByUserIdSorted(currentUser.getId());
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Page<OrderDTO>> getOrdersByStatus(@PathVariable String status, Pageable pageable) {
        Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
        Page<OrderDTO> orderDTOs = orders.map(this::convertToDTO);
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<List<OrderDTO>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Order> orders = orderService.getOrdersByDateRange(startDate, endDate);
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @GetMapping("/count/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<Long> countOrdersByStatus(@PathVariable String status) {
        Long count = orderService.countOrdersByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Double> calculateTotalRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Double revenue = orderService.calculateTotalRevenue(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/user/active")
    public ResponseEntity<List<OrderDTO>> getActiveOrdersByUser(@CurrentUser UserPrincipal currentUser) {
        List<Order> orders = orderService.getActiveOrdersByUserId(currentUser.getId());
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Order order = orderService.updateOrderStatus(id, status);
        OrderDTO orderDTO = convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/{id}/payment-status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('STAFF')")
    public ResponseEntity<OrderDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String paymentStatus) {
        Order order = orderService.updatePaymentStatus(id, paymentStatus);
        OrderDTO orderDTO = convertToDTO(order);
        return ResponseEntity.ok(orderDTO);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setUsername(order.getUser().getUsername());
        orderDTO.setTotalAmount(order.getTotalAmount());
        orderDTO.setShippingAddress(order.getShippingAddress());
        orderDTO.setPhone(order.getPhone());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setPaymentMethod(order.getPaymentMethod());
        orderDTO.setPaymentStatus(order.getPaymentStatus());
        orderDTO.setNote(order.getNote());
        orderDTO.setCreatedAt(order.getCreatedAt());
        orderDTO.setUpdatedAt(order.getUpdatedAt());

        // Convert order items
        List<OrderItemDTO> orderItemDTOs = order.getOrderItems().stream()
                .map(this::convertToOrderItemDTO)
                .collect(Collectors.toList());

        orderDTO.setItems(orderItemDTOs);

        return orderDTO;
    }

    private OrderItemDTO convertToOrderItemDTO(OrderItem orderItem) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setId(orderItem.getId());
        itemDTO.setOrderId(orderItem.getOrder().getId());
        itemDTO.setQuantity(orderItem.getQuantity());
        itemDTO.setUnitPrice(orderItem.getUnitPrice());
        itemDTO.setTotalPrice(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        // Set product details if available
        if (orderItem.getProduct() != null) {
            itemDTO.setProductId(orderItem.getProduct().getId());
            itemDTO.setProductName(orderItem.getProduct().getName());
            itemDTO.setProductImageUrl(orderItem.getProduct().getImageUrl());
        }

        // Set pet details if available
        if (orderItem.getPet() != null) {
            itemDTO.setPetId(orderItem.getPet().getId());
            itemDTO.setPetName(orderItem.getPet().getName());
            itemDTO.setPetImageUrl(orderItem.getPet().getImageUrl());
        }

        return itemDTO;
    }
}
