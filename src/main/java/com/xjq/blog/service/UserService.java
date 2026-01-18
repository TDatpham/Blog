package com.xjq.blog.service;

import com.xjq.blog.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User checkUser(String username, String password);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    User saveUser(User user);
    String registerUser(String username, String email, String password);
    Optional<User> findById(Long id);
    User findByUsername(String username);
    User findByEmail(String email);
    void updateUserStatus(Long userId, boolean enabled);
    void deleteUser(Long userId);

    List<User> findAllUsers();
}
