package com.xjq.blog.web;

import com.xjq.blog.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/ask")
    public String askQuestion(@RequestParam String userInput) {
        System.out.println("Nhận được yêu cầu: " + userInput); // Log input
        return chatbotService.getResponse(userInput);
    }
}
