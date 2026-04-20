package com.truongduchoang.SpringBootRESTfullAPIs.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.UserCreateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.request.UserUpdateRequest;
import com.truongduchoang.SpringBootRESTfullAPIs.dto.response.UserResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.ApiResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.services.UserService;

import jakarta.validation.Valid;

@RestController
public class UserController {
   public final UserService userService;

   public UserController(UserService userService) {
      this.userService = userService;
   }

   @PostMapping(value = "/api/users", consumes = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<ApiResponse<UserResponse>> createApiUser(@Valid @RequestBody UserCreateRequest request) {
      UserResponse createdUser = userService.createUser(request, null);
      ApiResponse<UserResponse> result = new ApiResponse<>(
            HttpStatus.CREATED,
            "Create user successfully",
            createdUser,
            null
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
   }

   @PostMapping(value = "/api/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<ApiResponse<UserResponse>> createApiUserWithAvatar(
         @Valid @ModelAttribute UserCreateRequest request,
         @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
      UserResponse createdUser = userService.createUser(request, avatar);
      ApiResponse<UserResponse> result = new ApiResponse<>(
            HttpStatus.CREATED,
            "Create user successfully",
            createdUser,
            null
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
   }

   @GetMapping("/api/users")
   public ResponseEntity<ApiResponse<List<UserResponse>>> getApiUsers() {
      List<UserResponse> users = userService.getAllUserResponses();
      ApiResponse<List<UserResponse>> result = new ApiResponse<>(
            HttpStatus.OK,
            "Get users successfully",
            users,
            null
      );
      return ResponseEntity.ok(result);
   }

   @GetMapping("/api/users/{id}")
   public ResponseEntity<ApiResponse<UserResponse>> getApiUserById(@PathVariable Long id) {
      UserResponse user = userService.getUserResponseById(id);
      ApiResponse<UserResponse> result = new ApiResponse<>(
            HttpStatus.OK,
            "Get user successfully",
            user,
            null
      );
      return ResponseEntity.ok(result);
   }

   @PutMapping(value = "/api/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
   public ResponseEntity<ApiResponse<UserResponse>> updateApiUser(
         @PathVariable Long id,
         @Valid @RequestBody UserUpdateRequest request) {
      UserResponse updatedUser = userService.updateUser(id, request, null);
      ApiResponse<UserResponse> result = new ApiResponse<>(
            HttpStatus.OK,
            "Update user successfully",
            updatedUser,
            null
      );
      return ResponseEntity.ok(result);
   }

   @PutMapping(value = "/api/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   public ResponseEntity<ApiResponse<UserResponse>> updateApiUserWithAvatar(
         @PathVariable Long id,
         @Valid @ModelAttribute UserUpdateRequest request,
         @RequestPart(value = "avatar", required = false) MultipartFile avatar) {
      UserResponse updatedUser = userService.updateUser(id, request, avatar);
      ApiResponse<UserResponse> result = new ApiResponse<>(
            HttpStatus.OK,
            "Update user successfully",
            updatedUser,
            null
      );
      return ResponseEntity.ok(result);
   }

   @DeleteMapping("/api/users/{id}")
   public ResponseEntity<ApiResponse<Void>> deleteApiUser(@PathVariable long id) {
      userService.getUserResponseById(id);
      userService.deleteUser(id);
      ApiResponse<Void> result = new ApiResponse<>(
            HttpStatus.NO_CONTENT,
            "Delete user successfully",
            null,
            null
      );
      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
   }

   @PostMapping("/users")
   public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
      User createdUser = userService.createUser(user);
      ApiResponse<User> result = new ApiResponse<>(
            HttpStatus.CREATED,
            "Create a user successful",
            createdUser,
            null
      );
      return ResponseEntity.status(HttpStatus.CREATED).body(result);
   }

   @GetMapping("/users")
   public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
      List<User> listUser = this.userService.getAllUsers();
      ApiResponse<List<User>> result = new ApiResponse<>(
            HttpStatus.OK,
            "Get list of users successful",
            listUser,
            null
      );
      return ResponseEntity.ok(result);
   }

   @GetMapping("/users/{id}")
   public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable long id) {
      Optional<User> userById = this.userService.getUserById(id);

      if (userById.isPresent()) {
         ApiResponse<User> result = new ApiResponse<>(
               HttpStatus.OK,
               "Get user by id " + id + " successful",
               userById.get(),
               null
         );
         return ResponseEntity.ok(result);
      } else {
         ApiResponse<User> result = new ApiResponse<>(
               HttpStatus.NOT_FOUND,
               "User with id " + id + " not found",
               null,
               "USER_NOT_FOUND"
         );
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
      }
   }

   @PutMapping("/users/{id}")
   public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable long id, @Valid @RequestBody User user) {
      try {
         User updatedUser = this.userService.updateUser(id, user);

         ApiResponse<User> result = new ApiResponse<>(
               HttpStatus.OK,
               "Update user with id " + id + " successful",
               updatedUser,
               null
         );

         return ResponseEntity.ok(result);
      } catch (NoSuchElementException e) {
         ApiResponse<User> result = new ApiResponse<>(
               HttpStatus.NOT_FOUND,
               "User with id " + id + " not found",
               null,
               e.getMessage()
         );

         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
      }
   }

   @DeleteMapping("/users/{id}")
   public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable long id) {
      this.userService.deleteUser(id);

      ApiResponse<Void> result = new ApiResponse<>(
            HttpStatus.NO_CONTENT,
            "Delete user with id " + id + " successful",
            null,
            null
      );

      return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
   }
}
