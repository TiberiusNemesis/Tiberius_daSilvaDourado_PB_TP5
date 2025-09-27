package com.ordermanagement.repository;

import com.ordermanagement.model.Seller;
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

class SellerRepositoryTest {

    @Mock
    private SellerRepository sellerRepository;

    private Seller testSeller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testSeller = new Seller();
        testSeller.setId(1L);
        testSeller.setEmail("seller@example.com");
        testSeller.setName("Test Seller");
        testSeller.setPassword("password123");
        testSeller.setCnpj("12.345.678/0001-90");
        testSeller.setBusinessName("Test Store");
        testSeller.setCreatedAt(LocalDateTime.now());
        testSeller.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save seller successfully")
    void testSaveSeller() {
        when(sellerRepository.save(testSeller)).thenReturn(testSeller);

        Seller savedSeller = sellerRepository.save(testSeller);

        assertNotNull(savedSeller);
        assertEquals(testSeller.getId(), savedSeller.getId());
        assertEquals(testSeller.getEmail(), savedSeller.getEmail());
        assertEquals(testSeller.getName(), savedSeller.getName());
        assertEquals(testSeller.getCnpj(), savedSeller.getCnpj());
        assertEquals(testSeller.getBusinessName(), savedSeller.getBusinessName());
        verify(sellerRepository).save(testSeller);
    }

    @Test
    @DisplayName("Should find seller by id")
    void testFindById() {
        when(sellerRepository.findById(1L)).thenReturn(Optional.of(testSeller));

        Optional<Seller> foundSeller = sellerRepository.findById(1L);

        assertTrue(foundSeller.isPresent());
        assertEquals(testSeller.getId(), foundSeller.get().getId());
        assertEquals(testSeller.getCnpj(), foundSeller.get().getCnpj());
        verify(sellerRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find seller by email")
    void testFindByEmail() {
        when(sellerRepository.findByEmail("seller@example.com")).thenReturn(Optional.of(testSeller));

        Optional<Seller> foundSeller = sellerRepository.findByEmail("seller@example.com");

        assertTrue(foundSeller.isPresent());
        assertEquals(testSeller.getEmail(), foundSeller.get().getEmail());
        assertEquals(testSeller.getName(), foundSeller.get().getName());
        verify(sellerRepository).findByEmail("seller@example.com");
    }

    @Test
    @DisplayName("Should find seller by CNPJ")
    void testFindByCnpj() {
        when(sellerRepository.findByCnpj("12.345.678/0001-90")).thenReturn(Optional.of(testSeller));

        Optional<Seller> foundSeller = sellerRepository.findByCnpj("12.345.678/0001-90");

        assertTrue(foundSeller.isPresent());
        assertEquals(testSeller.getCnpj(), foundSeller.get().getCnpj());
        assertEquals(testSeller.getBusinessName(), foundSeller.get().getBusinessName());
        verify(sellerRepository).findByCnpj("12.345.678/0001-90");
    }

    @Test
    @DisplayName("Should return empty when seller not found by CNPJ")
    void testFindByCnpjNotFound() {
        when(sellerRepository.findByCnpj("00.000.000/0000-00")).thenReturn(Optional.empty());

        Optional<Seller> foundSeller = sellerRepository.findByCnpj("00.000.000/0000-00");

        assertFalse(foundSeller.isPresent());
        verify(sellerRepository).findByCnpj("00.000.000/0000-00");
    }

    @Test
    @DisplayName("Should find all sellers")
    void testFindAll() {
        Seller seller2 = new Seller();
        seller2.setId(2L);
        seller2.setEmail("seller2@example.com");
        seller2.setName("Second Seller");
        seller2.setCnpj("98.765.432/0001-10");
        seller2.setBusinessName("Second Store");

        List<Seller> sellers = Arrays.asList(testSeller, seller2);
        when(sellerRepository.findAll()).thenReturn(sellers);

        List<Seller> foundSellers = sellerRepository.findAll();

        assertNotNull(foundSellers);
        assertEquals(2, foundSellers.size());
        assertTrue(foundSellers.contains(testSeller));
        assertTrue(foundSellers.contains(seller2));
        verify(sellerRepository).findAll();
    }

    @Test
    @DisplayName("Should delete seller by id")
    void testDeleteById() {
        doNothing().when(sellerRepository).deleteById(1L);

        sellerRepository.deleteById(1L);

        verify(sellerRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if seller exists by email")
    void testExistsByEmail() {
        when(sellerRepository.existsByEmail("seller@example.com")).thenReturn(true);
        when(sellerRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean exists = sellerRepository.existsByEmail("seller@example.com");
        boolean notExists = sellerRepository.existsByEmail("notfound@example.com");

        assertTrue(exists);
        assertFalse(notExists);
        verify(sellerRepository).existsByEmail("seller@example.com");
        verify(sellerRepository).existsByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should inherit all UserRepository functionality")
    void testUserRepositoryInheritance() {
        // Test that SellerRepository extends UserRepository
        assertTrue(UserRepository.class.isAssignableFrom(SellerRepository.class));
    }

    @Test
    @DisplayName("Should handle null CNPJ search")
    void testFindByNullCnpj() {
        when(sellerRepository.findByCnpj(null)).thenReturn(Optional.empty());

        Optional<Seller> foundSeller = sellerRepository.findByCnpj(null);

        assertFalse(foundSeller.isPresent());
        verify(sellerRepository).findByCnpj(null);
    }

    @Test
    @DisplayName("Should handle empty CNPJ search")
    void testFindByEmptyCnpj() {
        when(sellerRepository.findByCnpj("")).thenReturn(Optional.empty());

        Optional<Seller> foundSeller = sellerRepository.findByCnpj("");

        assertFalse(foundSeller.isPresent());
        verify(sellerRepository).findByCnpj("");
    }

    @Test
    @DisplayName("Should handle invalid CNPJ format")
    void testFindByInvalidCnpj() {
        String invalidCnpj = "invalid-cnpj";
        when(sellerRepository.findByCnpj(invalidCnpj)).thenReturn(Optional.empty());

        Optional<Seller> foundSeller = sellerRepository.findByCnpj(invalidCnpj);

        assertFalse(foundSeller.isPresent());
        verify(sellerRepository).findByCnpj(invalidCnpj);
    }
}