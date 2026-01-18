package com.xjq.blog.web.admin;

import com.xjq.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegisterController {

    @Autowired // Đảm bảo rằng Spring sẽ inject UserService vào đây
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {
        // Kiểm tra và gọi phương thức đăng ký từ UserService
        String result = userService.registerUser(username, email, password);
        model.addAttribute("message", result);
        return "register"; // Hoặc trang khác tùy theo kết quả
    }
}
