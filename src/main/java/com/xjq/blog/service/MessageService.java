package com.xjq.blog.service;

import com.xjq.blog.model.Message;
import com.xjq.blog.model.User;
import java.util.List;

public interface MessageService {
    List<Message> listMessage(User user);

    List<User> getRecentConversations(User user);

    List<Message> getConversation(User user, Long partnerId);

    Message saveMessage(Message message);
}
