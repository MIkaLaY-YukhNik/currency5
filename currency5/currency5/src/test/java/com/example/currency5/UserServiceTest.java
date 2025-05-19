package com.example.currency5.service;

import com.example.currency5.dto.UserBulkDTO;
import com.example.currency5.entity.User;
import com.example.currency5.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User("user1");
        user1.setId(1L);
        user2 = new User("user2");
        user2.setId(2L);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUserWhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        Optional<User> result = userService.getUserById(1L);
        assertTrue(result.isPresent());
        assertEquals(user1, result.get());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldReturnEmptyWhenNotExists() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<User> result = userService.getUserById(999L);
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void createUser_ShouldSaveAndReturnUser() {
        when(userRepository.save(user1)).thenReturn(user1);
        User result = userService.createUser(user1);
        assertEquals(user1, result);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void createBulkUsers_ShouldSaveAllUsers() {
        UserBulkDTO bulkDTO = new UserBulkDTO();
        bulkDTO.setUsers(Arrays.asList(new UserBulkDTO.UserDTO("user1"), new UserBulkDTO.UserDTO("user2")));
        when(userRepository.saveAll(anyList())).thenReturn(Arrays.asList(user1, user2));
        List<User> result = userService.createBulkUsers(bulkDTO);
        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
        verify(userRepository, times(1)).saveAll(anyList());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.save(user1)).thenReturn(user1);
        User updatedUser = new User("updated_user");
        updatedUser.setId(1L);
        User result = userService.updateUser(1L, updatedUser);
        assertEquals("updated_user", result.getUsername());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user1);
    }

    @Test
    void updateUser_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.updateUser(999L, new User()));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        userService.deleteUser(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(user1);
    }

    @Test
    void deleteUser_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.deleteUser(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).delete(any());
    }
}