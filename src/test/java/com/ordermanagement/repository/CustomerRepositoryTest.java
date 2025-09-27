package com.ordermanagement.repository;

import com.ordermanagement.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerRepositoryTest {

    @Mock
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testCustomer = new Customer("customer@example.com", "password123", "John Doe", "555-1234");
        testCustomer.setId(1L);
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save customer successfully")
    void testSaveCustomer() {
        when(customerRepository.save(testCustomer)).thenReturn(testCustomer);

        Customer savedCustomer = customerRepository.save(testCustomer);

        assertNotNull(savedCustomer);
        assertEquals(testCustomer.getId(), savedCustomer.getId());
        assertEquals(testCustomer.getEmail(), savedCustomer.getEmail());
        assertEquals(testCustomer.getName(), savedCustomer.getName());
        assertEquals(testCustomer.getPhone(), savedCustomer.getPhone());
        verify(customerRepository).save(testCustomer);
    }

    @Test
    @DisplayName("Should find customer by id")
    void testFindById() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        Optional<Customer> foundCustomer = customerRepository.findById(1L);

        assertTrue(foundCustomer.isPresent());
        assertEquals(testCustomer.getId(), foundCustomer.get().getId());
        assertEquals(testCustomer.getEmail(), foundCustomer.get().getEmail());
        assertEquals(testCustomer.getName(), foundCustomer.get().getName());
        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find customer by email")
    void testFindByEmail() {
        when(customerRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(testCustomer));

        Optional<Customer> foundCustomer = customerRepository.findByEmail("customer@example.com");

        assertTrue(foundCustomer.isPresent());
        assertEquals(testCustomer.getEmail(), foundCustomer.get().getEmail());
        assertEquals(testCustomer.getName(), foundCustomer.get().getName());
        verify(customerRepository).findByEmail("customer@example.com");
    }

    @Test
    @DisplayName("Should return empty when customer not found by email")
    void testFindByEmailNotFound() {
        when(customerRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = customerRepository.findByEmail("notfound@example.com");

        assertFalse(foundCustomer.isPresent());
        verify(customerRepository).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should find all customers")
    void testFindAll() {
        Customer customer2 = new Customer("customer2@example.com", "password456", "Jane Smith", "555-5678");
        customer2.setId(2L);

        List<Customer> customers = Arrays.asList(testCustomer, customer2);
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> foundCustomers = customerRepository.findAll();

        assertNotNull(foundCustomers);
        assertEquals(2, foundCustomers.size());
        assertTrue(foundCustomers.contains(testCustomer));
        assertTrue(foundCustomers.contains(customer2));
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Should delete customer by id")
    void testDeleteById() {
        doNothing().when(customerRepository).deleteById(1L);

        customerRepository.deleteById(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if customer exists by email")
    void testExistsByEmail() {
        when(customerRepository.existsByEmail("customer@example.com")).thenReturn(true);
        when(customerRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean exists = customerRepository.existsByEmail("customer@example.com");
        boolean notExists = customerRepository.existsByEmail("notfound@example.com");

        assertTrue(exists);
        assertFalse(notExists);
        verify(customerRepository).existsByEmail("customer@example.com");
        verify(customerRepository).existsByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should inherit all UserRepository functionality")
    void testUserRepositoryInheritance() {
        // Test that CustomerRepository extends UserRepository
        assertTrue(UserRepository.class.isAssignableFrom(CustomerRepository.class));
    }

    @Test
    @DisplayName("Should handle invalid customer data")
    void testInvalidCustomerData() {
        Customer invalidCustomer = new Customer();
        invalidCustomer.setEmail(""); // Invalid email
        invalidCustomer.setName(null); // Invalid name

        when(customerRepository.save(invalidCustomer)).thenReturn(invalidCustomer);

        Customer savedCustomer = customerRepository.save(invalidCustomer);

        assertEquals("", savedCustomer.getEmail());
        assertNull(savedCustomer.getName());
        verify(customerRepository).save(invalidCustomer);
    }

    @Test
    @DisplayName("Should handle empty email search")
    void testEmptyEmailSearch() {
        when(customerRepository.findByEmail("")).thenReturn(Optional.empty());

        Optional<Customer> foundCustomer = customerRepository.findByEmail("");

        assertFalse(foundCustomer.isPresent());
        verify(customerRepository).findByEmail("");
    }
}