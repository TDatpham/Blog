package com.xjq.blog.repository;

import com.xjq.blog.model.Message;
import com.xjq.blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiver(User receiver);

    List<Message> findBySender(User sender);

    List<Message> findByReceiverOrSender(User receiver, User sender);
}
