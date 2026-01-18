package com.xjq.blog.service;

import com.xjq.blog.model.User;
import com.xjq.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Async
    public void sendNewPostNotification(String postTitle, String postUrl) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Bài viết mới: " + postTitle);
            message.setText(
                    "Xin chào,\n\n" +
                            "Blog vừa có bài viết mới: " + postTitle + "\n" +
                            "Bạn có thể đọc bài viết tại đây: " + postUrl + "\n\n" +
                            "Cảm ơn bạn đã theo dõi blog!");

            mailSender.send(message);
        }
    }
}
