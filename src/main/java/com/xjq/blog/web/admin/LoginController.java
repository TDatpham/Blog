package com.xjq.blog.web.admin;

import com.xjq.blog.model.User;
import com.xjq.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Hiển thị form login tại /admin/login (loại bỏ /admin để tránh trùng với BlogController)
    @GetMapping({"", "/login"})
    public String loginPage() {
        return "admin/login";
    }




    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        RedirectAttributes attributes) {
        User user = userService.checkUser(username, password);
        if (user != null) {
            user.setPassword(null); // Ẩn mật khẩu
            session.setAttribute("user", user);
            return "admin/blogs";
        } else {
            attributes.addFlashAttribute("message", "Invalid Username or Password");
            return "redirect:/admin/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/admin/login";
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "admin/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes attributes) {
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            attributes.addFlashAttribute("message", "You must be logged in to change password");
            return "redirect:/admin/login";
        }

        // Kiểm tra mật khẩu hiện tại
        User user = userService.checkUser(currentUser.getUsername(), currentPassword);
        if (user == null) {
            attributes.addFlashAttribute("message", "Current password is incorrect");
            return "redirect:/admin/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            attributes.addFlashAttribute("message", "New passwords do not match");
            return "redirect:/admin/change-password";
        }

        // Lưu mật khẩu mới đã được mã hóa
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.saveUser(user);

        attributes.addFlashAttribute("message", "Password changed successfully. Please login again.");
        session.removeAttribute("user");
        return "redirect:/admin/login";
    }
}
