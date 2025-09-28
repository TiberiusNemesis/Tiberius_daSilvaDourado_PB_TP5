package com.ordermanagement.service;

import com.ordermanagement.model.DeliveryPerson;
import com.ordermanagement.model.Order;
import com.ordermanagement.enums.OrderStatus;
import com.ordermanagement.repository.DeliveryPersonRepository;
import com.ordermanagement.repository.OrderRepository;
import java.util.List;
import java.util.Optional;

public class DeliveryService {
    private DeliveryPersonRepository deliveryPersonRepository;
    private OrderRepository orderRepository;
    
    public DeliveryService(DeliveryPersonRepository deliveryPersonRepository,
                          OrderRepository orderRepository) {
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.orderRepository = orderRepository;
    }
    
    public List<DeliveryPerson> getAvailableDeliveryPersons() {
        return deliveryPersonRepository.findByAvailable(true);
    }
    
    public DeliveryPerson assignDeliveryPerson() {
        List<DeliveryPerson> availablePersons = getAvailableDeliveryPersons();
        if (!availablePersons.isEmpty()) {
            DeliveryPerson deliveryPerson = availablePersons.get(0);
            deliveryPerson.setAvailable(false);
            deliveryPersonRepository.save(deliveryPerson);
            return deliveryPerson;
        }
        throw new RuntimeException("No delivery person available");
    }
    
    public Order acceptDelivery(Long orderId, Long deliveryPersonId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        Optional<DeliveryPerson> deliveryPersonOpt = deliveryPersonRepository.findById(deliveryPersonId);
        
        if (orderOpt.isPresent() && deliveryPersonOpt.isPresent()) {
            Order order = orderOpt.get();
            DeliveryPerson deliveryPerson = deliveryPersonOpt.get();
            
            order.setDeliveryPerson(deliveryPerson);
            order.setStatus(OrderStatus.ON_THE_WAY);
            deliveryPerson.setAvailable(false);
            
            deliveryPersonRepository.save(deliveryPerson);
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order or Delivery Person not found");
    }
    
    public Order completeDelivery(Long orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(OrderStatus.DELIVERED);
            
            if (order.getDeliveryPerson() != null) {
                DeliveryPerson deliveryPerson = order.getDeliveryPerson();
                deliveryPerson.setAvailable(true);
                deliveryPersonRepository.save(deliveryPerson);
            }
            
            return orderRepository.save(order);
        }
        throw new RuntimeException("Order not found");
    }
    
    public List<Order> getDeliveryPersonOrders(DeliveryPerson deliveryPerson) {
        return orderRepository.findByDeliveryPerson(deliveryPerson);
    }
}