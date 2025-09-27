package com.ordermanagement.repository;

import com.ordermanagement.model.User;
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

class UserRepositoryTest {

    @Mock
    private UserRepository<User> userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User() {};
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("password123");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should save user successfully")
    void testSaveUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User savedUser = userRepository.save(testUser);

        assertNotNull(savedUser);
        assertEquals(testUser.getId(), savedUser.getId());
        assertEquals(testUser.getEmail(), savedUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should find user by id")
    void testFindById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userRepository.findById(1L);

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return empty when user not found by id")
    void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findById(999L);

        assertFalse(foundUser.isPresent());
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find user by email")
    void testFindByEmail() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getEmail(), foundUser.get().getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void testFindByEmailNotFound() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        Optional<User> foundUser = userRepository.findByEmail("notfound@example.com");

        assertFalse(foundUser.isPresent());
        verify(userRepository).findByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should find all users")
    void testFindAll() {
        User user2 = new User() {};
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setName("User Two");

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userRepository.findAll();

        assertNotNull(foundUsers);
        assertEquals(2, foundUsers.size());
        assertTrue(foundUsers.contains(testUser));
        assertTrue(foundUsers.contains(user2));
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should delete user by id")
    void testDeleteById() {
        doNothing().when(userRepository).deleteById(1L);

        userRepository.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void testExistsByEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("notfound@example.com")).thenReturn(false);

        boolean exists = userRepository.existsByEmail("test@example.com");
        boolean notExists = userRepository.existsByEmail("notfound@example.com");

        assertTrue(exists);
        assertFalse(notExists);
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository).existsByEmail("notfound@example.com");
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void testNullParameters() {
        when(userRepository.findById(null)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(userRepository.existsByEmail(null)).thenReturn(false);

        Optional<User> userById = userRepository.findById(null);
        Optional<User> userByEmail = userRepository.findByEmail(null);
        boolean exists = userRepository.existsByEmail(null);

        assertFalse(userById.isPresent());
        assertFalse(userByEmail.isPresent());
        assertFalse(exists);
    }
}