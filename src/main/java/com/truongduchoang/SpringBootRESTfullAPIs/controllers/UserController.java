package com.truongduchoang.SpringBootRESTfullAPIs.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.truongduchoang.SpringBootRESTfullAPIs.models.User;
import com.truongduchoang.SpringBootRESTfullAPIs.services.UserService;

@RestController
public class UserController {
    public final UserService userService;

     public UserController(UserService userService){
        this.userService = userService;
     }

     @PostMapping("/users")
     public ResponseEntity<User> createUser(@RequestBody User user){
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
     }

     @GetMapping("/users")
     public ResponseEntity<List<User>> getAllUsers(){
        return ResponseEntity.ok(this.userService.getAllUsers());
     }

     @GetMapping("/users/{id}")
     public ResponseEntity<User> getUserById(@PathVariable long id){
        Optional<User> userById = this.userService.getUserById(id);
        if(userById.isPresent()){
            return ResponseEntity.ok().body(userById.get());
        }else{
            return ResponseEntity.notFound().build();
        }
     }

     @PutMapping("/users/{id}")
     public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User user){
        User updatedUser = this.userService.updateUser(id, user);
        return ResponseEntity.ok().body(updatedUser);
     }

     @DeleteMapping("/users/{id}")
     public ResponseEntity<Void> deleteUser(@PathVariable long id){
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
     }
}
