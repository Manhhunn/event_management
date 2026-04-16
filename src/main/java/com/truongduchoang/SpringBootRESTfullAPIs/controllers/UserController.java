package com.truongduchoang.SpringBootRESTfullAPIs.controllers;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.truongduchoang.SpringBootRESTfullAPIs.models.ApiResponse;
import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.services.UserService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
public class UserController {
   public final UserService userService;

   public UserController(UserService userService) {
      this.userService = userService;
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
   public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable long id, @RequestBody User user) {
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