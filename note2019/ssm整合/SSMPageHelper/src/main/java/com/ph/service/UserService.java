package com.ph.service;

import com.ph.pojo.User;

public interface UserService {
    Integer insertUser(User user);
    User queryUser(User user);
}
