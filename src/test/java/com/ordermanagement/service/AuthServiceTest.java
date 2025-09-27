package com.ordermanagement.service;

import com.ordermanagement.model.Customer;
import com.ordermanagement.model.Seller;
import com.ordermanagement.model.DeliveryPerson;
import com.ordermanagement.repository.CustomerRepository;
import com.ordermanagement.repository.SellerRepository;
import com.ordermanagement.repository.DeliveryPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    private Customer customer;
    private Seller seller;
    private DeliveryPerson deliveryPerson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(customerRepository, sellerRepository, deliveryPersonRepository);

        customer = new Customer("customer@example.com", "password123", "John Customer", "555-1234");
        customer.setId(1L);

        seller = new Seller("seller@example.com", "password123", "Jane Seller", "555-5678", "Test Business", "12345678901");
        seller.setId(1L);

        deliveryPerson = new DeliveryPerson("delivery@example.com", "password123", "Bob Delivery", "555-9999", "Motorcycle", "ABC-1234");
        deliveryPerson.setId(1L);
    }

    @Test
    @DisplayName("Should login customer successfully with valid credentials")
    void testLoginCustomerSuccess() {
        when(customerRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

        Customer result = authService.loginCustomer("customer@example.com", "password123");

        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        assertEquals(customer.getEmail(), result.getEmail());
        verify(customerRepository, times(1)).findByEmail("customer@example.com");
    }

    @Test
    @DisplayName("Should throw exception when customer not found")
    void testLoginCustomerNotFound() {
        when(customerRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            authService.loginCustomer("notfound@example.com", "password123")
        );
        verify(customerRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should throw exception when customer password is incorrect")
    void testLoginCustomerWrongPassword() {
        when(customerRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

        assertThrows(RuntimeException.class, () ->
            authService.loginCustomer("customer@example.com", "wrongpassword")
        );
        verify(customerRepository, times(1)).findByEmail("customer@example.com");
    }

    @Test
    @DisplayName("Should login seller successfully with valid credentials")
    void testLoginSellerSuccess() {
        when(sellerRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));

        Seller result = authService.loginSeller("seller@example.com", "password123");

        assertNotNull(result);
        assertEquals(seller.getId(), result.getId());
        assertEquals(seller.getEmail(), result.getEmail());
        verify(sellerRepository, times(1)).findByEmail("seller@example.com");
    }

    @Test
    @DisplayName("Should throw exception when seller not found")
    void testLoginSellerNotFound() {
        when(sellerRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            authService.loginSeller("notfound@example.com", "password123")
        );
        verify(sellerRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should throw exception when seller password is incorrect")
    void testLoginSellerWrongPassword() {
        when(sellerRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(seller));

        assertThrows(RuntimeException.class, () ->
            authService.loginSeller("seller@example.com", "wrongpassword")
        );
        verify(sellerRepository, times(1)).findByEmail("seller@example.com");
    }

    @Test
    @DisplayName("Should login delivery person successfully with valid credentials")
    void testLoginDeliveryPersonSuccess() {
        when(deliveryPersonRepository.findByEmail("delivery@example.com")).thenReturn(Optional.of(deliveryPerson));

        DeliveryPerson result = authService.loginDeliveryPerson("delivery@example.com", "password123");

        assertNotNull(result);
        assertEquals(deliveryPerson.getId(), result.getId());
        assertEquals(deliveryPerson.getEmail(), result.getEmail());
        verify(deliveryPersonRepository, times(1)).findByEmail("delivery@example.com");
    }

    @Test
    @DisplayName("Should throw exception when delivery person not found")
    void testLoginDeliveryPersonNotFound() {
        when(deliveryPersonRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            authService.loginDeliveryPerson("notfound@example.com", "password123")
        );
        verify(deliveryPersonRepository, times(1)).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should throw exception when delivery person password is incorrect")
    void testLoginDeliveryPersonWrongPassword() {
        when(deliveryPersonRepository.findByEmail("delivery@example.com")).thenReturn(Optional.of(deliveryPerson));

        assertThrows(RuntimeException.class, () ->
            authService.loginDeliveryPerson("delivery@example.com", "wrongpassword")
        );
        verify(deliveryPersonRepository, times(1)).findByEmail("delivery@example.com");
    }

    @Test
    @DisplayName("Should register customer successfully")
    void testRegisterCustomerSuccess() {
        when(customerRepository.existsByEmail("newcustomer@example.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = authService.registerCustomer("newcustomer@example.com", "password123", "New Customer", "555-0000");

        assertNotNull(result);
        verify(customerRepository, times(1)).existsByEmail("newcustomer@example.com");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should throw exception when registering customer with existing email")
    void testRegisterCustomerEmailExists() {
        when(customerRepository.existsByEmail("customer@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            authService.registerCustomer("customer@example.com", "password123", "New Customer", "555-0000")
        );
        verify(customerRepository, times(1)).existsByEmail("customer@example.com");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should register seller successfully")
    void testRegisterSellerSuccess() {
        when(sellerRepository.existsByEmail("newseller@example.com")).thenReturn(false);
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller result = authService.registerSeller("newseller@example.com", "password123", "New Seller", "555-0000", "New Business", "98765432101");

        assertNotNull(result);
        verify(sellerRepository, times(1)).existsByEmail("newseller@example.com");
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    @DisplayName("Should throw exception when registering seller with existing email")
    void testRegisterSellerEmailExists() {
        when(sellerRepository.existsByEmail("seller@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            authService.registerSeller("seller@example.com", "password123", "New Seller", "555-0000", "New Business", "98765432101")
        );
        verify(sellerRepository, times(1)).existsByEmail("seller@example.com");
        verify(sellerRepository, never()).save(any(Seller.class));
    }

    @Test
    @DisplayName("Should register delivery person successfully")
    void testRegisterDeliveryPersonSuccess() {
        when(deliveryPersonRepository.existsByEmail("newdelivery@example.com")).thenReturn(false);
        when(deliveryPersonRepository.save(any(DeliveryPerson.class))).thenReturn(deliveryPerson);

        DeliveryPerson result = authService.registerDeliveryPerson("newdelivery@example.com", "password123", "New Delivery", "555-0000", "Car", "XYZ-5678");

        assertNotNull(result);
        verify(deliveryPersonRepository, times(1)).existsByEmail("newdelivery@example.com");
        verify(deliveryPersonRepository, times(1)).save(any(DeliveryPerson.class));
    }

    @Test
    @DisplayName("Should throw exception when registering delivery person with existing email")
    void testRegisterDeliveryPersonEmailExists() {
        when(deliveryPersonRepository.existsByEmail("delivery@example.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            authService.registerDeliveryPerson("delivery@example.com", "password123", "New Delivery", "555-0000", "Car", "XYZ-5678")
        );
        verify(deliveryPersonRepository, times(1)).existsByEmail("delivery@example.com");
        verify(deliveryPersonRepository, never()).save(any(DeliveryPerson.class));
    }
}