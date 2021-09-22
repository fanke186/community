package com.fanke.community.service;

import com.fanke.community.dao.UserMapper;
import com.fanke.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserService
 * @Author Fanke
 * @Created 2021/9/21 9:42
 */

@Service
public class UserService {

    private final UserMapper userMapper;

    @Autowired(required = false)
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }



}
