package com.xjq.blog.web;

import com.xjq.blog.model.Message;
import com.xjq.blog.model.User;
import com.xjq.blog.service.MessageService;
import com.xjq.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @GetMapping("/messages")
    public String messages(@RequestParam(required = false) Long recipientId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin";
        }

        // List of people user has chatted with
        model.addAttribute("conversations", messageService.getRecentConversations(user));

        // If a recipient is selected, load the specific conversation
        if (recipientId != null) {
            model.addAttribute("currentConversation", messageService.getConversation(user, recipientId));
            User recipient = userService.findById(recipientId).orElse(null);
            model.addAttribute("recipient", recipient);
        } else {
            // Fallback or empty state
            model.addAttribute("currentConversation", java.util.Collections.emptyList());
        }

        // List of all users to start new chat
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("recipientId", recipientId);
        return "messages";
    }

    @PostMapping("/messages")
    public String postMessage(@RequestParam Long receiverId, @RequestParam String content, HttpSession session,
            RedirectAttributes attributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/admin";
        }
        User receiver = userService.findById(receiverId).orElse(null);
        if (receiver != null) {
            Message message = new Message();
            message.setSender(user);
            message.setReceiver(receiver);
            message.setContent(content);
            messageService.saveMessage(message);
            attributes.addFlashAttribute("message", "Message sent successfully");
        } else {
            attributes.addFlashAttribute("message", "User not found");
        }
        return "redirect:/messages?recipientId=" + receiverId;
    }
}
