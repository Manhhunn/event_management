package com.truongduchoang.SpringBootRESTfullAPIs.services;

import java.util.List;
import java.util.Optional;

import com.truongduchoang.SpringBootRESTfullAPIs.models.User;

public interface UserService {
    User createUser(User user);

    List<User> getAllUsers();

    Optional<User> getUserById(long id);

    User updateUser(long id, User updatedUser);

    void deleteUser(long id);
}
