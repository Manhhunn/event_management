package com.truongduchoang.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.UserRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.services.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void createUser_shouldReturnUser_whenEmailValid() {
        User inputUser = new User(null, "demo1@gmail.com", "truong duc hoang");
        User outputUser = new User(1L, "demo1@gmail.com", "truong duc hoang");
        when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(false);
        when(this.userRepository.save(any())).thenReturn(outputUser);

        User resultUser = this.userService.createUser(inputUser);

        assertEquals(1L, resultUser.getId());
    }

    @Test
    public void createUser_shouldThrowException_whenEmailInvalid() {
        User inputUser = new User(null, "demo1@gmail.com", "truong duc hoang");
        
        when(this.userRepository.existsByEmail(inputUser.getEmail())).thenReturn(true);
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            this.userService.createUser(inputUser);
        });

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    public void getAllUsers(){
        List<User> outputUsers = new ArrayList<>();
        outputUsers.add(new User(1L, "demo@gmail.com", "Truong duc hoang"));
        outputUsers.add(new User(2L, "demo2@gmail.com", "Truong duc hoang 2"));

        when(this.userRepository.findAll()).thenReturn(outputUsers);

        List<User> resultUsers = this.userService.getAllUsers();

        assertEquals(2, resultUsers.size());
        assertEquals("demo2@gmail.com", resultUsers.get(1).getEmail());
    }

    @Test
    public void getUserById_shouldReturnOptionalUser(){
        Long inputId = 1L;
        User inputUser = new User(1L, "demo@gmail.com", "Truong DuC Hoang");
        Optional<User> outputOptionalUser = Optional.of(inputUser);

        when(this.userRepository.findById(inputId)).thenReturn(outputOptionalUser);

        Optional<User> resultUser = this.userService.getUserById(inputId);

        assertEquals(true, resultUser.isPresent());
    }

    @Test
    public void deleteUser_shouldReturnVoid_whenUserExist(){
        Long inputId = 1L;
        when(this.userRepository.existsById(inputId)).thenReturn(true);

        this.userService.deleteUser(inputId);

        verify(this.userRepository).deleteById(inputId);
    }

    @Test
    public void deleteUser_shouldReturnException_whenUserNotExist(){
        Long inputId = 1L;
        when(this.userRepository.existsById(inputId)).thenReturn(false);

        Exception ex = assertThrows(NoSuchElementException.class, () -> {
            this.userService.deleteUser(inputId);
        });

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    public void updateUser_shouldReturnUser_whenValid(){
        Long inputId = 1L;
        User inputUser = new User(1L, "oldEmail@gmail.com", "old name");
        User outputUser = new User(1L, "newEmail@gmail.com", "new name");
        when(this.userRepository.findById(inputId)).thenReturn(Optional.of(inputUser));
        when(this.userRepository.save(any())).thenReturn(outputUser);

        User resultUser = this.userService.updateUser(inputId, inputUser);

        assertEquals("new name", resultUser.getName());
    }
}