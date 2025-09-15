package com.ordermanagement.repository;

import com.ordermanagement.model.Order;
import com.ordermanagement.model.Customer;
import com.ordermanagement.model.DeliveryPerson;
import com.ordermanagement.enums.OrderStatus;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAll();
    List<Order> findByCustomer(Customer customer);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByDeliveryPerson(DeliveryPerson deliveryPerson);
    void deleteById(Long id);
}