package com.ph.mapper;

import com.ph.pojo.User;

public interface UserMapper {
    Integer insertUser(User user);
    User queryUser(User user);
}
