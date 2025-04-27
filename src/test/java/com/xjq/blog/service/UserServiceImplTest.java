package com.xjq.blog.service;

import com.xjq.blog.dao.UserRepository;
import com.xjq.blog.po.User;
import com.xjq.blog.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
    }

    @Test
    void testCheckUser_Success() {
        // Giả lập phương thức findByUsernameAndPassword trả về user hợp lệ
        when(userRepository.findByUsernameAndPassword("testuser", "hashedpassword")).thenReturn(user);

        User result = userService.checkUser("testuser", "hashedpassword");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findByUsernameAndPassword("testuser", "hashedpassword");
    }

    @Test
    void testCheckUser_Failure() {
        // Giả lập phương thức findByUsernameAndPassword trả về null khi thông tin không khớp
        when(userRepository.findByUsernameAndPassword("testuser", "wrongpassword")).thenReturn(null);

        User result = userService.checkUser("testuser", "wrongpassword");

        assertNull(result);
        verify(userRepository, times(1)).findByUsernameAndPassword("testuser", "wrongpassword");
    }
}
