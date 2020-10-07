package com.ph.service.serviceImp;

import com.ph.mapper.UserMapper;
import com.ph.pojo.User;
import com.ph.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public Integer insertUser(User user) {
        return userMapper.insertUser(user);
    }

    @Override
    public User queryUser(User user) {
        return userMapper.queryUser(user);
    }
}
