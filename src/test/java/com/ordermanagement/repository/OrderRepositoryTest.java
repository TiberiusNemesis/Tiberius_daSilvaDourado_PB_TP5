package com.ordermanagement.repository;

import com.ordermanagement.model.Order;
import com.ordermanagement.model.Customer;
import com.ordermanagement.model.DeliveryPerson;
import com.ordermanagement.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderRepositoryTest {

    @Mock
    private OrderRepository orderRepository;

    private Order testOrder;
    private Customer testCustomer;
    private DeliveryPerson testDeliveryPerson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCustomer = new Customer("customer@example.com", "password123", "John Doe", "555-1234");
        testCustomer.setId(1L);

        testDeliveryPerson = new DeliveryPerson();
        testDeliveryPerson.setId(1L);
        testDeliveryPerson.setName("Delivery Person");
        testDeliveryPerson.setAvailable(true);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomer(testCustomer);
        testOrder.setStatus(OrderStatus.WAITING);
        testOrder.setDeliveryFee(new BigDecimal("5.00"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save order successfully")
    void testSaveOrder() {
        when(orderRepository.save(testOrder)).thenReturn(testOrder);

        Order savedOrder = orderRepository.save(testOrder);

        assertNotNull(savedOrder);
        assertEquals(testOrder.getId(), savedOrder.getId());
        assertEquals(testOrder.getCustomer().getId(), savedOrder.getCustomer().getId());
        assertEquals(testOrder.getStatus(), savedOrder.getStatus());
        assertEquals(testOrder.getDeliveryFee(), savedOrder.getDeliveryFee());
        verify(orderRepository).save(testOrder);
    }

    @Test
    @DisplayName("Should find order by id")
    void testFindById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        Optional<Order> foundOrder = orderRepository.findById(1L);

        assertTrue(foundOrder.isPresent());
        assertEquals(testOrder.getId(), foundOrder.get().getId());
        assertEquals(testOrder.getStatus(), foundOrder.get().getStatus());
        verify(orderRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when order not found by id")
    void testFindByIdNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Order> foundOrder = orderRepository.findById(999L);

        assertFalse(foundOrder.isPresent());
        verify(orderRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find all orders")
    void testFindAll() {
        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomer(testCustomer);
        order2.setStatus(OrderStatus.IN_PREPARATION);

        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> foundOrders = orderRepository.findAll();

        assertNotNull(foundOrders);
        assertEquals(2, foundOrders.size());
        assertTrue(foundOrders.contains(testOrder));
        assertTrue(foundOrders.contains(order2));
        verify(orderRepository).findAll();
    }

    @Test
    @DisplayName("Should find orders by customer")
    void testFindByCustomer() {
        Order order2 = new Order();
        order2.setId(2L);
        order2.setCustomer(testCustomer);
        order2.setStatus(OrderStatus.DELIVERED);

        List<Order> customerOrders = Arrays.asList(testOrder, order2);
        when(orderRepository.findByCustomer(testCustomer)).thenReturn(customerOrders);

        List<Order> foundOrders = orderRepository.findByCustomer(testCustomer);

        assertNotNull(foundOrders);
        assertEquals(2, foundOrders.size());
        foundOrders.forEach(order -> assertEquals(testCustomer.getId(), order.getCustomer().getId()));
        verify(orderRepository).findByCustomer(testCustomer);
    }

    @Test
    @DisplayName("Should return empty list when no orders found for customer")
    void testFindByCustomerEmpty() {
        Customer otherCustomer = new Customer();
        otherCustomer.setId(999L);

        when(orderRepository.findByCustomer(otherCustomer)).thenReturn(Arrays.asList());

        List<Order> foundOrders = orderRepository.findByCustomer(otherCustomer);

        assertNotNull(foundOrders);
        assertTrue(foundOrders.isEmpty());
        verify(orderRepository).findByCustomer(otherCustomer);
    }

    @Test
    @DisplayName("Should find orders by status")
    void testFindByStatus() {
        Order waitingOrder1 = new Order();
        waitingOrder1.setId(1L);
        waitingOrder1.setStatus(OrderStatus.WAITING);

        Order waitingOrder2 = new Order();
        waitingOrder2.setId(2L);
        waitingOrder2.setStatus(OrderStatus.WAITING);

        List<Order> waitingOrders = Arrays.asList(waitingOrder1, waitingOrder2);
        when(orderRepository.findByStatus(OrderStatus.WAITING)).thenReturn(waitingOrders);

        List<Order> foundOrders = orderRepository.findByStatus(OrderStatus.WAITING);

        assertNotNull(foundOrders);
        assertEquals(2, foundOrders.size());
        foundOrders.forEach(order -> assertEquals(OrderStatus.WAITING, order.getStatus()));
        verify(orderRepository).findByStatus(OrderStatus.WAITING);
    }

    @Test
    @DisplayName("Should find orders by delivery person")
    void testFindByDeliveryPerson() {
        testOrder.setDeliveryPerson(testDeliveryPerson);
        testOrder.setStatus(OrderStatus.ON_THE_WAY);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setDeliveryPerson(testDeliveryPerson);
        order2.setStatus(OrderStatus.DELIVERED);

        List<Order> deliveryPersonOrders = Arrays.asList(testOrder, order2);
        when(orderRepository.findByDeliveryPerson(testDeliveryPerson)).thenReturn(deliveryPersonOrders);

        List<Order> foundOrders = orderRepository.findByDeliveryPerson(testDeliveryPerson);

        assertNotNull(foundOrders);
        assertEquals(2, foundOrders.size());
        foundOrders.forEach(order -> assertEquals(testDeliveryPerson.getId(), order.getDeliveryPerson().getId()));
        verify(orderRepository).findByDeliveryPerson(testDeliveryPerson);
    }

    @Test
    @DisplayName("Should return empty list when no orders found for delivery person")
    void testFindByDeliveryPersonEmpty() {
        DeliveryPerson otherDeliveryPerson = new DeliveryPerson();
        otherDeliveryPerson.setId(999L);

        when(orderRepository.findByDeliveryPerson(otherDeliveryPerson)).thenReturn(Arrays.asList());

        List<Order> foundOrders = orderRepository.findByDeliveryPerson(otherDeliveryPerson);

        assertNotNull(foundOrders);
        assertTrue(foundOrders.isEmpty());
        verify(orderRepository).findByDeliveryPerson(otherDeliveryPerson);
    }

    @Test
    @DisplayName("Should delete order by id")
    void testDeleteById() {
        doNothing().when(orderRepository).deleteById(1L);

        orderRepository.deleteById(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle finding orders with different statuses")
    void testFindByDifferentStatuses() {
        when(orderRepository.findByStatus(OrderStatus.WAITING)).thenReturn(Arrays.asList(testOrder));
        when(orderRepository.findByStatus(OrderStatus.IN_PREPARATION)).thenReturn(Arrays.asList());
        when(orderRepository.findByStatus(OrderStatus.DELIVERED)).thenReturn(Arrays.asList());

        List<Order> waitingOrders = orderRepository.findByStatus(OrderStatus.WAITING);
        List<Order> preparingOrders = orderRepository.findByStatus(OrderStatus.IN_PREPARATION);
        List<Order> deliveredOrders = orderRepository.findByStatus(OrderStatus.DELIVERED);

        assertEquals(1, waitingOrders.size());
        assertEquals(OrderStatus.WAITING, waitingOrders.get(0).getStatus());
        assertTrue(preparingOrders.isEmpty());
        assertTrue(deliveredOrders.isEmpty());

        verify(orderRepository).findByStatus(OrderStatus.WAITING);
        verify(orderRepository).findByStatus(OrderStatus.IN_PREPARATION);
        verify(orderRepository).findByStatus(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void testNullParameters() {
        when(orderRepository.findById(null)).thenReturn(Optional.empty());
        when(orderRepository.findByCustomer(null)).thenReturn(Arrays.asList());
        when(orderRepository.findByStatus(null)).thenReturn(Arrays.asList());
        when(orderRepository.findByDeliveryPerson(null)).thenReturn(Arrays.asList());

        Optional<Order> orderById = orderRepository.findById(null);
        List<Order> ordersByCustomer = orderRepository.findByCustomer(null);
        List<Order> ordersByStatus = orderRepository.findByStatus(null);
        List<Order> ordersByDeliveryPerson = orderRepository.findByDeliveryPerson(null);

        assertFalse(orderById.isPresent());
        assertTrue(ordersByCustomer.isEmpty());
        assertTrue(ordersByStatus.isEmpty());
        assertTrue(ordersByDeliveryPerson.isEmpty());
    }

    @Test
    @DisplayName("Should save order with all status transitions")
    void testSaveOrderWithDifferentStatuses() {
        Order orderWaiting = new Order();
        orderWaiting.setStatus(OrderStatus.WAITING);

        Order orderInPreparation = new Order();
        orderInPreparation.setStatus(OrderStatus.IN_PREPARATION);

        Order orderOutForDelivery = new Order();
        orderOutForDelivery.setStatus(OrderStatus.ON_THE_WAY);

        Order orderDelivered = new Order();
        orderDelivered.setStatus(OrderStatus.DELIVERED);

        Order orderCancelled = new Order();
        orderCancelled.setStatus(OrderStatus.CANCELLED);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order savedWaiting = orderRepository.save(orderWaiting);
        Order savedInPreparation = orderRepository.save(orderInPreparation);
        Order savedOutForDelivery = orderRepository.save(orderOutForDelivery);
        Order savedDelivered = orderRepository.save(orderDelivered);
        Order savedCancelled = orderRepository.save(orderCancelled);

        assertEquals(OrderStatus.WAITING, savedWaiting.getStatus());
        assertEquals(OrderStatus.IN_PREPARATION, savedInPreparation.getStatus());
        assertEquals(OrderStatus.ON_THE_WAY, savedOutForDelivery.getStatus());
        assertEquals(OrderStatus.DELIVERED, savedDelivered.getStatus());
        assertEquals(OrderStatus.CANCELLED, savedCancelled.getStatus());
    }
}