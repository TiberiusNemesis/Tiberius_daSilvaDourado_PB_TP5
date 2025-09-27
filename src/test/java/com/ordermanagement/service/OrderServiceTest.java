package com.ordermanagement.service;

import com.ordermanagement.model.*;
import com.ordermanagement.enums.OrderStatus;
import com.ordermanagement.enums.PaymentMethod;
import com.ordermanagement.enums.ProductCategory;
import com.ordermanagement.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private PaymentService paymentService;

    private Customer customer;
    private Address address;
    private Product product;
    private Order order;
    private Seller seller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderService(orderRepository, productService, paymentService);

        customer = new Customer("john@example.com", "password", "John Doe", "555-1234");
        customer.setId(1L);

        address = new Address("Main St", "123", "Downtown", "City", "ST", "12345");
        address.setId(1L);

        Seller seller = new Seller("seller@example.com", "password", "Test Seller", "555-5678", "Test Business", "123456789");
        seller.setId(1L);
        product = new Product("Test Product", "Description", new BigDecimal("25.00"), ProductCategory.OTHER, seller);
        product.setId(1L);

        order = new Order(customer, address);
        order.setId(1L);
    }

    @Test
    @DisplayName("Should create order successfully")
    void testCreateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(customer, address);

        assertNotNull(result);
        assertEquals(customer, result.getCustomer());
        assertEquals(address, result.getDeliveryAddress());
        assertEquals(OrderStatus.WAITING, result.getStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should add item to order successfully")
    void testAddItemToOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.addItemToOrder(1L, 1L, 2, "No onions");

        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        assertEquals(product, result.getItems().get(0).getProduct());
        assertEquals(2, result.getItems().get(0).getQuantity());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should throw exception when adding item to non-waiting order")
    void testAddItemToNonWaitingOrder() {
        order.setStatus(OrderStatus.IN_PREPARATION);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productService.getProductById(1L)).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class, () ->
            orderService.addItemToOrder(1L, 1L, 2, null)
        );
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void testAddItemToNonExistentOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            orderService.addItemToOrder(1L, 1L, 2, null)
        );
    }

    @Test
    @DisplayName("Should finalize order successfully")
    void testFinalizeOrder() {
        PaymentCard paymentCard = new PaymentCard("4111111111111111", "John Doe", "12/25", "123", PaymentMethod.CREDIT_CARD);
        BigDecimal deliveryFee = new BigDecimal("5.00");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(paymentService.processPayment(any())).thenReturn(new Payment());

        Order result = orderService.finalizeOrder(1L, PaymentMethod.CREDIT_CARD,
                                                  paymentCard, deliveryFee, "DISCOUNT10");

        assertNotNull(result);
        assertEquals(PaymentMethod.CREDIT_CARD, result.getPaymentMethod());
        assertEquals(paymentCard, result.getPaymentCard());
        assertEquals(deliveryFee, result.getDeliveryFee());
        assertEquals("DISCOUNT10", result.getCouponCode());
        verify(paymentService, times(1)).processPayment(any(Order.class));
    }

    @Test
    @DisplayName("Should cancel order successfully")
    void testCancelOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.cancelOrder(1L, "Customer request");

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals("Customer request", result.getCancellationReason());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should not cancel already delivered order")
    void testCannotCancelDeliveredOrder() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.cancelOrder(1L, "Too late");

        assertNotNull(result);
        assertEquals(OrderStatus.DELIVERED, result.getStatus()); // Status should remain the same
        assertNull(result.getCancellationReason()); // No cancellation reason should be set
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should get orders by customer")
    void testGetOrdersByCustomer() {
        List<Order> orders = Arrays.asList(order, new Order(customer, address));
        when(orderRepository.findByCustomer(customer)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByCustomer(customer);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findByCustomer(customer);
    }

    @Test
    @DisplayName("Should get order by ID")
    void testGetOrderById() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById(1L);

        assertTrue(result.isPresent());
        assertEquals(order, result.get());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update order status")
    void testUpdateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L, OrderStatus.IN_PREPARATION);

        assertNotNull(result);
        assertEquals(OrderStatus.IN_PREPARATION, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    @DisplayName("Should get orders by status")
    void testGetOrdersByStatus() {
        List<Order> orders = Arrays.asList(order);
        when(orderRepository.findByStatus(OrderStatus.WAITING)).thenReturn(orders);

        List<Order> result = orderService.getOrdersByStatus(OrderStatus.WAITING);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(order, result.get(0));
        verify(orderRepository, times(1)).findByStatus(OrderStatus.WAITING);
    }

    @Test
    @DisplayName("Should assign delivery person to order")
    void testAssignDeliveryPerson() {
        DeliveryPerson deliveryPerson = new DeliveryPerson("delivery@example.com", "password", "Delivery Guy", "555-9999", "Motorcycle", "ABC-1234");
        deliveryPerson.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.assignDeliveryPerson(1L, deliveryPerson);

        assertNotNull(result);
        assertEquals(deliveryPerson, result.getDeliveryPerson());
        assertEquals(OrderStatus.ON_THE_WAY, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}