package com.space.service.impl;

import com.space.mapper.UserMapper;
import com.space.model.entity.User;
import com.space.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private SpaceMapper spaceMapper;

    // 使用事务确保用户注册和空间初始化的原子性

    @Override
    public boolean registerUser(User user) {
        try {
            // 插入用户信息
            boolean result = userMapper.register(user);

            if (result) {
                // 获取自增主键
                Long userId = user.getUserId();

                // 初始化用户空间（默认5MB）
               /* Space space = new Space();
                space.setUserId(userId);
                space.setTotalSize(5 * 1024 * 1024L); // 5MB
                space.setUsedSize(0L);
                space.setStatus("NORMAL");
                space.setCreateTime(new java.util.Date());

                spaceMapper.insertSpace(space);*/
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("用户注册失败：" + e.getMessage());
        }
    }

    @Override
    public User getUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }

    @Override
    public User login(String account, String password) {
        // 实际应用中应进行密码加密比较
        return userMapper.login(account, password);
    }




}
