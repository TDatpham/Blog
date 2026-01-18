package com.xjq.blog.web.admin;

import com.xjq.blog.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendMailToAuthor(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String message) {
        emailService.sendMail(name, email, message);
        return "Gửi thư thành công! Cảm ơn bạn đã liên hệ.";
    }
}

