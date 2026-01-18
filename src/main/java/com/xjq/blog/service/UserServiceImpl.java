package com.xjq.blog.service;

import com.xjq.blog.repository.UserRepository;
import com.xjq.blog.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User checkUser(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            System.out.println("User not found");
            return null;
        }
        if (!user.isEnabled()) {
            System.out.println("User not enabled");
            return null;
        }
        if (passwordEncoder.matches(rawPassword, user.getPassword())) {
            System.out.println("User authenticated");
            return user;
        } else {
            System.out.println("Password does not match");
            return null;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User saveUser(User user) {
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) { // Kiểm tra xem đã mã hóa chưa
                                                                                    // (BCrypt)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void setEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setEnabled(enabled);
            userRepository.save(user);
        }
    }

    @Override
    public String registerUser(String username, String email, String password) {
        if (existsByUsername(username)) {
            return "Username already exists";
        }

        if (existsByEmail(email)) {
            return "Email already exists";
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        // Mã hóa mật khẩu trước khi lưu
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRole("USER");
        user.setNickname(username);
        user.setAvatar("/images/avatar.png"); // Mặc định

        userRepository.save(user);
        return "Registration successful!";
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void updateUserStatus(Long userId, boolean enabled) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setEnabled(enabled);
            userRepository.save(user);
        });
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
