package com.ordermanagement.repository;

import com.ordermanagement.model.DeliveryPerson;
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

class DeliveryPersonRepositoryTest {

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    private DeliveryPerson testDeliveryPerson;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testDeliveryPerson = new DeliveryPerson();
        testDeliveryPerson.setId(1L);
        testDeliveryPerson.setEmail("delivery@example.com");
        testDeliveryPerson.setName("John Delivery");
        testDeliveryPerson.setPassword("password123");
        testDeliveryPerson.setPhone("555-1234");
        testDeliveryPerson.setVehicleType("Motorcycle");
        testDeliveryPerson.setAvailable(true);
        testDeliveryPerson.setCreatedAt(LocalDateTime.now());
        testDeliveryPerson.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save delivery person successfully")
    void testSaveDeliveryPerson() {
        when(deliveryPersonRepository.save(testDeliveryPerson)).thenReturn(testDeliveryPerson);

        DeliveryPerson savedDeliveryPerson = deliveryPersonRepository.save(testDeliveryPerson);

        assertNotNull(savedDeliveryPerson);
        assertEquals(testDeliveryPerson.getId(), savedDeliveryPerson.getId());
        assertEquals(testDeliveryPerson.getEmail(), savedDeliveryPerson.getEmail());
        assertEquals(testDeliveryPerson.getName(), savedDeliveryPerson.getName());
        assertEquals(testDeliveryPerson.getVehicleType(), savedDeliveryPerson.getVehicleType());
        assertEquals(testDeliveryPerson.isAvailable(), savedDeliveryPerson.isAvailable());
        verify(deliveryPersonRepository).save(testDeliveryPerson);
    }

    @Test
    @DisplayName("Should find delivery person by id")
    void testFindById() {
        when(deliveryPersonRepository.findById(1L)).thenReturn(Optional.of(testDeliveryPerson));

        Optional<DeliveryPerson> foundDeliveryPerson = deliveryPersonRepository.findById(1L);

        assertTrue(foundDeliveryPerson.isPresent());
        assertEquals(testDeliveryPerson.getId(), foundDeliveryPerson.get().getId());
        assertEquals(testDeliveryPerson.getVehicleType(), foundDeliveryPerson.get().getVehicleType());
        verify(deliveryPersonRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find delivery person by email")
    void testFindByEmail() {
        when(deliveryPersonRepository.findByEmail("delivery@example.com")).thenReturn(Optional.of(testDeliveryPerson));

        Optional<DeliveryPerson> foundDeliveryPerson = deliveryPersonRepository.findByEmail("delivery@example.com");

        assertTrue(foundDeliveryPerson.isPresent());
        assertEquals(testDeliveryPerson.getEmail(), foundDeliveryPerson.get().getEmail());
        assertEquals(testDeliveryPerson.getName(), foundDeliveryPerson.get().getName());
        verify(deliveryPersonRepository).findByEmail("delivery@example.com");
    }

    @Test
    @DisplayName("Should find delivery persons by availability")
    void testFindByAvailable() {
        DeliveryPerson unavailableDeliveryPerson = new DeliveryPerson();
        unavailableDeliveryPerson.setId(2L);
        unavailableDeliveryPerson.setEmail("unavailable@example.com");
        unavailableDeliveryPerson.setName("Jane Delivery");
        unavailableDeliveryPerson.setAvailable(false);

        List<DeliveryPerson> availablePersons = Arrays.asList(testDeliveryPerson);
        List<DeliveryPerson> unavailablePersons = Arrays.asList(unavailableDeliveryPerson);

        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(availablePersons);
        when(deliveryPersonRepository.findByAvailable(false)).thenReturn(unavailablePersons);

        List<DeliveryPerson> foundAvailable = deliveryPersonRepository.findByAvailable(true);
        List<DeliveryPerson> foundUnavailable = deliveryPersonRepository.findByAvailable(false);

        assertEquals(1, foundAvailable.size());
        assertTrue(foundAvailable.get(0).isAvailable());
        assertEquals(testDeliveryPerson.getId(), foundAvailable.get(0).getId());

        assertEquals(1, foundUnavailable.size());
        assertFalse(foundUnavailable.get(0).isAvailable());
        assertEquals(unavailableDeliveryPerson.getId(), foundUnavailable.get(0).getId());

        verify(deliveryPersonRepository).findByAvailable(true);
        verify(deliveryPersonRepository).findByAvailable(false);
    }

    @Test
    @DisplayName("Should return empty list when no delivery persons found by availability")
    void testFindByAvailableEmpty() {
        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(Arrays.asList());

        List<DeliveryPerson> foundDeliveryPersons = deliveryPersonRepository.findByAvailable(true);

        assertNotNull(foundDeliveryPersons);
        assertTrue(foundDeliveryPersons.isEmpty());
        verify(deliveryPersonRepository).findByAvailable(true);
    }

    @Test
    @DisplayName("Should find all delivery persons")
    void testFindAll() {
        DeliveryPerson deliveryPerson2 = new DeliveryPerson();
        deliveryPerson2.setId(2L);
        deliveryPerson2.setEmail("delivery2@example.com");
        deliveryPerson2.setName("Jane Delivery");
        deliveryPerson2.setVehicleType("Bicycle");
        deliveryPerson2.setAvailable(false);

        List<DeliveryPerson> deliveryPersons = Arrays.asList(testDeliveryPerson, deliveryPerson2);
        when(deliveryPersonRepository.findAll()).thenReturn(deliveryPersons);

        List<DeliveryPerson> foundDeliveryPersons = deliveryPersonRepository.findAll();

        assertNotNull(foundDeliveryPersons);
        assertEquals(2, foundDeliveryPersons.size());
        assertTrue(foundDeliveryPersons.contains(testDeliveryPerson));
        assertTrue(foundDeliveryPersons.contains(deliveryPerson2));
        verify(deliveryPersonRepository).findAll();
    }

    @Test
    @DisplayName("Should delete delivery person by id")
    void testDeleteById() {
        doNothing().when(deliveryPersonRepository).deleteById(1L);

        deliveryPersonRepository.deleteById(1L);

        verify(deliveryPersonRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if delivery person exists by email")
    void testExistsByEmail() {
        when(deliveryPersonRepository.existsByEmail("delivery@example.com")).thenReturn(true);
        when(deliveryPersonRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean exists = deliveryPersonRepository.existsByEmail("delivery@example.com");
        boolean notExists = deliveryPersonRepository.existsByEmail("notfound@example.com");

        assertTrue(exists);
        assertFalse(notExists);
        verify(deliveryPersonRepository).existsByEmail("delivery@example.com");
        verify(deliveryPersonRepository).existsByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should inherit all UserRepository functionality")
    void testUserRepositoryInheritance() {
        // Test that DeliveryPersonRepository extends UserRepository
        assertTrue(UserRepository.class.isAssignableFrom(DeliveryPersonRepository.class));
    }

    @Test
    @DisplayName("Should handle finding available delivery persons with mixed availability")
    void testFindByAvailableMixed() {
        DeliveryPerson available1 = new DeliveryPerson();
        available1.setId(1L);
        available1.setAvailable(true);

        DeliveryPerson available2 = new DeliveryPerson();
        available2.setId(2L);
        available2.setAvailable(true);

        List<DeliveryPerson> availablePersons = Arrays.asList(available1, available2);
        when(deliveryPersonRepository.findByAvailable(true)).thenReturn(availablePersons);

        List<DeliveryPerson> foundAvailable = deliveryPersonRepository.findByAvailable(true);

        assertEquals(2, foundAvailable.size());
        foundAvailable.forEach(person -> assertTrue(person.isAvailable()));
        verify(deliveryPersonRepository).findByAvailable(true);
    }

    @Test
    @DisplayName("Should handle saving delivery person with different vehicle types")
    void testSaveWithDifferentVehicleTypes() {
        DeliveryPerson bicycleDelivery = new DeliveryPerson();
        bicycleDelivery.setVehicleType("Bicycle");
        bicycleDelivery.setAvailable(true);

        DeliveryPerson carDelivery = new DeliveryPerson();
        carDelivery.setVehicleType("Car");
        carDelivery.setAvailable(false);

        when(deliveryPersonRepository.save(bicycleDelivery)).thenReturn(bicycleDelivery);
        when(deliveryPersonRepository.save(carDelivery)).thenReturn(carDelivery);

        DeliveryPerson savedBicycle = deliveryPersonRepository.save(bicycleDelivery);
        DeliveryPerson savedCar = deliveryPersonRepository.save(carDelivery);

        assertEquals("Bicycle", savedBicycle.getVehicleType());
        assertTrue(savedBicycle.isAvailable());
        assertEquals("Car", savedCar.getVehicleType());
        assertFalse(savedCar.isAvailable());
    }
}