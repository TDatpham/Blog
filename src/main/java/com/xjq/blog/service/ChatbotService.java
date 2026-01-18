package com.xjq.blog.service;

import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    public String getResponse(String userInput) {
        // Loại bỏ các ký tự trắng ở đầu và cuối input
        userInput = userInput.trim().toLowerCase();

        if (userInput.isEmpty()) {
            return "Sorry, you didn't enter anything.";
        }
        if (userInput.contains("hello") || userInput.contains("hi")) {
            return "Hi there! I'm the Blog Assistant. How can I help you today?";
        } else if (userInput.contains("about")) {
            return "This is a personal blog created by Thanh Dat Pham to share knowledge about Java, Spring Boot, and Web Development.";
        } else if (userInput.contains("author") || userInput.contains("who are you")) {
            return "The author of this blog is Thanh Dat Pham, a passionate software developer.";
        } else if (userInput.contains("post") && userInput.contains("how")) {
            return "To write a blog post, you need to Register and then Login. Once logged in, go to the Admin dashboard to publish your thoughts!";
        } else if (userInput.contains("topics") || userInput.contains("category") || userInput.contains("type")) {
            return "We cover various topics including Java, Spring Boot, Semantic UI, and general Software Engineering tips.";
        } else if (userInput.contains("subscribe")) {
            return "While we don't have a newsletter yet, you can register an account to receive updates in the future!";
        } else if (userInput.contains("latest")) {
            return "The very latest posts are always shown on the Index page and in the footer section.";
        } else if (userInput.contains("comment")) {
            return "Feel free to leave comments on any post! We love to hear your feedback.";
        } else if (userInput.contains("contact")) {
            return "You can reach the author via email at phamthanhdat2003vl1@gmail.com or via LinkedIn.";
        } else if (userInput.contains("share")) {
            return "You can share posts to Facebook using the link at the bottom of each blog article!";
        } else if (userInput.contains("search")) {
            return "Use the magnifying glass icon at the top right to search our entire blog database.";
        } else if (userInput.contains("register") || userInput.contains("sign up")) {
            return "Click 'Subscribe' in the menu to join our community.";
        } else if (userInput.contains("thank")) {
            return "You're very welcome! Let me know if you need anything else.";
        } else {
            return "That's an interesting question! I don't have a specific answer for that yet, but you can try asking about 'author', 'topics', or 'how to post'.";
        }
    }
}
