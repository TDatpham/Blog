package com.xjq.blog.service;

import com.xjq.blog.model.Message;
import com.xjq.blog.model.User;
import com.xjq.blog.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> listMessage(User user) {
        return messageRepository.findByReceiverOrSender(user, user);
    }

    @Override
    public List<User> getRecentConversations(User user) {
        // Logic to get distinct users who have chatted with the current user.
        // For simplicity, fetching all messages and extracting users. Ideally, use a
        // custom query.
        List<Message> messages = messageRepository.findByReceiverOrSender(user, user);
        java.util.Set<User> users = new java.util.HashSet<>();
        for (Message msg : messages) {
            if (!msg.getSender().getId().equals(user.getId())) {
                users.add(msg.getSender());
            }
            if (!msg.getReceiver().getId().equals(user.getId())) {
                users.add(msg.getReceiver());
            }
        }
        return new java.util.ArrayList<>(users);
    }

    @Override
    public List<Message> getConversation(User user, Long partnerId) {
        // Get all messages where specific user is sender or receiver with partner
        List<Message> allMessages = messageRepository.findByReceiverOrSender(user, user);
        List<Message> conversation = new java.util.ArrayList<>();
        for (Message msg : allMessages) {
            boolean condition1 = msg.getSender().getId().equals(user.getId())
                    && msg.getReceiver().getId().equals(partnerId);
            boolean condition2 = msg.getSender().getId().equals(partnerId)
                    && msg.getReceiver().getId().equals(user.getId());
            if (condition1 || condition2) {
                conversation.add(msg);
            }
        }
        // sort by time
        conversation.sort((m1, m2) -> m1.getCreateTime().compareTo(m2.getCreateTime()));
        return conversation;
    }

    @Transactional
    @Override
    public Message saveMessage(Message message) {
        message.setCreateTime(new Date());
        return messageRepository.save(message);
    }
}
