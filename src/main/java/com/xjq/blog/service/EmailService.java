package com.xjq.blog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String senderName, String senderEmail, String messageContent) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail); // Người gửi (người dùng nhập)
        message.setTo("phamthanhdat2003vl1@gmail.com"); // Email tác giả (bạn)
        message.setSubject("Thư góp ý từ " + senderName);
        message.setText("Người gửi: " + senderName + "\nEmail: " + senderEmail + "\n\nNội dung:\n" + messageContent);

        mailSender.send(message);
    }
}

