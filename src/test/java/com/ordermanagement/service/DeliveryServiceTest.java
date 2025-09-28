package com.ordermanagement.service;

import com.ordermanagement.model.DeliveryPerson;
import com.ordermanagement.model.Order;
import com.ordermanagement.model.Customer;
import com.ordermanagement.model.Address;
import com.ordermanagement.enums.OrderStatus;
import com.ordermanagement.repository.DeliveryPersonRepository;
import com.ordermanagement.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    private DeliveryService deliveryService;

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    @Mock
    private OrderRepository orderRepository;

    private DeliveryPerson deliveryPerson1;
    private DeliveryPerson deliveryPerson2;
    private Order order;
    private Customer customer;
    private Address address;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deliveryService = new DeliveryService(deliveryPersonRepository, orderRepository);

        deliveryPerson1 = new DeliveryPerson("delivery1@example.com", "password", "John Delivery", "555-1111", "Motorcycle", "ABC-1234");
        deliveryPerson1.setId(1L);
        deliveryPerson1.setAvailable(true);

        deliveryPerson2 = new DeliveryPerson("delivery2@example.com", "password", "Jane Delivery", "555-2222", "Car", "XYZ-5678");
        deliveryPerson2.setId(2L);
        deliveryPerson2.setAvailable(true);

        customer = new Customer("customer@example.com", "password", "John Customer", "555-3333");
        customer.setId(1L);

        address = new Address("Main St", "123", "Downtown", "City", "ST", "12345");
        address.setId(1L);

        order = new Order(customer, address);
        order.setId(1L);
        order.setStatus(OrderStatus.IN_PREPARATION);
    }

    @Test
    @DisplayName("Should get available delivery persons")
    void testGetAvailableDeliveryPersons() {
        List<DeliveryPerson> availablePersons = List.of(deliveryPerson1, deliveryPerson2);
        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(availablePersons);

        List<DeliveryPerson> result = deliveryService.getAvailableDeliveryPersons();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(deliveryPerson1));
        assertTrue(result.contains(deliveryPerson2));
        verify(deliveryPersonRepository, times(1)).findByAvailable(true);
    }

    @Test
    @DisplayName("Should return empty list when no delivery persons available")
    void testGetAvailableDeliveryPersonsEmpty() {
        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(new ArrayList<>());

        List<DeliveryPerson> result = deliveryService.getAvailableDeliveryPersons();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(deliveryPersonRepository, times(1)).findByAvailable(true);
    }

    @Test
    @DisplayName("Should assign delivery person successfully")
    void testAssignDeliveryPersonSuccess() {
        List<DeliveryPerson> availablePersons = List.of(deliveryPerson1, deliveryPerson2);
        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(availablePersons);
        when(deliveryPersonRepository.save(deliveryPerson1)).thenReturn(deliveryPerson1);

        DeliveryPerson result = deliveryService.assignDeliveryPerson();

        assertNotNull(result);
        assertEquals(deliveryPerson1.getId(), result.getId());
        assertFalse(result.isAvailable());
        verify(deliveryPersonRepository, times(1)).findByAvailable(true);
        verify(deliveryPersonRepository, times(1)).save(deliveryPerson1);
    }

    @Test
    @DisplayName("Should throw exception when no delivery person available for assignment")
    void testAssignDeliveryPersonNoAvailable() {
        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(new ArrayList<>());

        assertThrows(RuntimeException.class, () ->
            deliveryService.assignDeliveryPerson()
        );
        verify(deliveryPersonRepository, times(1)).findByAvailable(true);
        verify(deliveryPersonRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should accept delivery successfully")
    void testAcceptDeliverySuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(deliveryPersonRepository.findById(1L)).thenReturn(Optional.of(deliveryPerson1));
        when(deliveryPersonRepository.save(deliveryPerson1)).thenReturn(deliveryPerson1);
        when(orderRepository.save(order)).thenReturn(order);

        Order result = deliveryService.acceptDelivery(1L, 1L);

        assertNotNull(result);
        assertEquals(deliveryPerson1, result.getDeliveryPerson());
        assertEquals(OrderStatus.ON_THE_WAY, result.getStatus());
        assertFalse(deliveryPerson1.isAvailable());
        verify(orderRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, times(1)).save(deliveryPerson1);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw exception when order not found for accepting delivery")
    void testAcceptDeliveryOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        when(deliveryPersonRepository.findById(1L)).thenReturn(Optional.of(deliveryPerson1));

        assertThrows(RuntimeException.class, () ->
            deliveryService.acceptDelivery(1L, 1L)
        );
        verify(orderRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when delivery person not found for accepting delivery")
    void testAcceptDeliveryPersonNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(deliveryPersonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            deliveryService.acceptDelivery(1L, 1L)
        );
        verify(orderRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should complete delivery successfully")
    void testCompleteDeliverySuccess() {
        order.setDeliveryPerson(deliveryPerson1);
        deliveryPerson1.setAvailable(false);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(deliveryPersonRepository.save(deliveryPerson1)).thenReturn(deliveryPerson1);
        when(orderRepository.save(order)).thenReturn(order);

        Order result = deliveryService.completeDelivery(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        assertTrue(deliveryPerson1.isAvailable());
        verify(orderRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, times(1)).save(deliveryPerson1);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should complete delivery successfully without delivery person")
    void testCompleteDeliveryWithoutDeliveryPerson() {
        order.setDeliveryPerson(null);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        Order result = deliveryService.completeDelivery(1L);

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, never()).save(any());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw exception when order not found for completing delivery")
    void testCompleteDeliveryOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            deliveryService.completeDelivery(1L)
        );
        verify(orderRepository, times(1)).findById(1L);
        verify(deliveryPersonRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get delivery person orders")
    void testGetDeliveryPersonOrders() {
        List<Order> orders = List.of(order);
        when(orderRepository.findByDeliveryPerson(deliveryPerson1)).thenReturn(orders);

        List<Order> result = deliveryService.getDeliveryPersonOrders(deliveryPerson1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderRepository, times(1)).findByDeliveryPerson(deliveryPerson1);
    }

    @Test
    @DisplayName("Should return empty list when delivery person has no orders")
    void testGetDeliveryPersonOrdersEmpty() {
        when(orderRepository.findByDeliveryPerson(deliveryPerson1)).thenReturn(new ArrayList<>());

        List<Order> result = deliveryService.getDeliveryPersonOrders(deliveryPerson1);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findByDeliveryPerson(deliveryPerson1);
    }
}