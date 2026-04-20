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

import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.UserCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.UserUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.UserResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.DuplicateResourceException;
import com.truongduchoang.SpringBootRESTfullAPIs.errors.ResourceNotFoundException;
import com.truongduchoang.SpringBootRESTfullAPIs.mapper.UserMapper;
import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.RoleRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.repository.UserRepository;
import com.truongduchoang.SpringBootRESTfullAPIs.services.CloudinaryService;
import com.truongduchoang.SpringBootRESTfullAPIs.services.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private UserServiceImpl userService;

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

    @Test
    public void createApiUser_shouldReturnUserResponse_whenEmailValid() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFullName("duc hoang");
        request.setEmail("api@gmail.com");

        User user = new User(null, "api@gmail.com", "duc hoang");
        User savedUser = new User(1L, "api@gmail.com", "duc hoang");
        UserResponse outputResponse = new UserResponse();
        outputResponse.setUserId(1L);
        outputResponse.setEmail("api@gmail.com");

        when(this.userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(this.userMapper.toEntity(request, null)).thenReturn(user);
        when(this.userRepository.save(user)).thenReturn(savedUser);
        when(this.userMapper.toResponse(savedUser)).thenReturn(outputResponse);

        UserResponse resultUser = this.userService.createUser(request, null);

        assertEquals(1L, resultUser.getUserId());
        assertEquals("api@gmail.com", resultUser.getEmail());
    }

    @Test
    public void createApiUser_shouldThrowException_whenEmailDuplicated() {
        UserCreateRequest request = new UserCreateRequest();
        request.setFullName("duc hoang");
        request.setEmail("api@gmail.com");

        when(this.userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        Exception ex = assertThrows(DuplicateResourceException.class, () -> {
            this.userService.createUser(request, null);
        });

        assertEquals("Email already exists", ex.getMessage());
    }

    @Test
    public void getUserResponseById_shouldReturnUserResponse_whenUserExist() {
        Long inputId = 1L;
        User inputUser = new User(1L, "demo@gmail.com", "Truong DuC Hoang");
        UserResponse outputResponse = new UserResponse();
        outputResponse.setUserId(1L);
        outputResponse.setEmail("demo@gmail.com");

        when(this.userRepository.findById(inputId)).thenReturn(Optional.of(inputUser));
        when(this.userMapper.toResponse(inputUser)).thenReturn(outputResponse);

        UserResponse resultUser = this.userService.getUserResponseById(inputId);

        assertEquals(1L, resultUser.getUserId());
        assertEquals("demo@gmail.com", resultUser.getEmail());
    }

    @Test
    public void getUserResponseById_shouldThrowException_whenUserNotExist() {
        Long inputId = 1L;
        when(this.userRepository.findById(inputId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ResourceNotFoundException.class, () -> {
            this.userService.getUserResponseById(inputId);
        });

        assertEquals("User with id 1 not found", ex.getMessage());
    }

    @Test
    public void updateApiUser_shouldReturnUserResponse_whenValid() {
        Long inputId = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("new@gmail.com");
        request.setFullName("new name");

        User inputUser = new User(1L, "old@gmail.com", "old name");
        User savedUser = new User(1L, "new@gmail.com", "new name");
        UserResponse outputResponse = new UserResponse();
        outputResponse.setUserId(1L);
        outputResponse.setEmail("new@gmail.com");

        when(this.userRepository.findById(inputId)).thenReturn(Optional.of(inputUser));
        when(this.userRepository.existsByEmailAndUserIdNot(request.getEmail(), inputId)).thenReturn(false);
        when(this.userRepository.save(inputUser)).thenReturn(savedUser);
        when(this.userMapper.toResponse(savedUser)).thenReturn(outputResponse);

        UserResponse resultUser = this.userService.updateUser(inputId, request, null);

        assertEquals("new@gmail.com", resultUser.getEmail());
    }

    @Test
    public void updateApiUser_shouldThrowException_whenEmailDuplicated() {
        Long inputId = 1L;
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("new@gmail.com");

        User inputUser = new User(1L, "old@gmail.com", "old name");
        when(this.userRepository.findById(inputId)).thenReturn(Optional.of(inputUser));
        when(this.userRepository.existsByEmailAndUserIdNot(request.getEmail(), inputId)).thenReturn(true);

        Exception ex = assertThrows(DuplicateResourceException.class, () -> {
            this.userService.updateUser(inputId, request, null);
        });

        assertEquals("Email already exists", ex.getMessage());
    }
}
