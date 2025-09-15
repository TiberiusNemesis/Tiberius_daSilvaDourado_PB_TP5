package com.ordermanagement.service;

import com.ordermanagement.model.*;
import com.ordermanagement.enums.OrderStatus;
import com.ordermanagement.enums.PaymentMethod;
import com.ordermanagement.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class OrderService {
    private OrderRepository orderRepository;
    private ProductService productService;
    private PaymentService paymentService;
    
    public OrderService(OrderRepository orderRepository, 
                       ProductService productService,
                       PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.paymentService = paymentService;
    }
    
    public Order createOrder(Customer customer, Address deliveryAddress) {
        Order order = new Order(customer, deliveryAddress);
        return orderRepository.save(order);
    }
    
    public Order addItemToOrder(Long orderId, Long productId, int quantity, String observations) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Optional<Product> productOpt = productService.getProductById(productId);
        
        if (orderOpt.isPresent() && productOpt.isPresent()) {
            Order order = orderOpt.get();
            Product product = productOpt.get();
            
            if (order.getStatus() == OrderStatus.WAITING) {
                OrderItem item = new OrderItem(product, quantity, observations);
                order.addItem(item);
                return orderRepository.save(order);
            } else {
                throw new RuntimeException("Cannot modify order that is not in waiting status");
            }
        }
        throw new RuntimeException("Order or Product not found");
    }
    
    public Order finalizeOrder(Long orderId, PaymentMethod paymentMethod, 
                             PaymentCard paymentCard, BigDecimal deliveryFee, String couponCode) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setPaymentMethod(paymentMethod);
            order.setPaymentCard(paymentCard);
            order.setDeliveryFee(deliveryFee);
            order.setCouponCode(couponCode);
            
            Order savedOrder = orderRepository.save(order);
            
            try {
                paymentService.processPayment(savedOrder);
                savedOrder.setStatus(OrderStatus.IN_PREPARATION);
                return orderRepository.save(savedOrder);
            } catch (Exception e) {
                throw new RuntimeException("Payment failed: " + e.getMessage());
            }
        }
        throw new RuntimeException("Order not found");
    }
    
    public Order cancelOrder(Long orderId, String reason) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.cancel(reason);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
    
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(newStatus);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
    
    public List<Order> getOrdersByCustomer(Customer customer) {
        return orderRepository.findByCustomer(customer);
    }
    
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Order assignDeliveryPerson(Long orderId, DeliveryPerson deliveryPerson) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setDeliveryPerson(deliveryPerson);
            order.setStatus(OrderStatus.ON_THE_WAY);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
}