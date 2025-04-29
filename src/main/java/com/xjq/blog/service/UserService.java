package com.xjq.blog.service;

import com.xjq.blog.model.User;

public interface UserService {
    User checkUser(String username, String password);
}
