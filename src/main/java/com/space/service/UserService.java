package com.space.service;

import com.space.model.entity.User;

public interface UserService {
    boolean registerUser(User user);
    User getUserById(Long userId);
    User login(String account, String password);
}
