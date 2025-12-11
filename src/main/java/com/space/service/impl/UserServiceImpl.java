package com.space.service.impl;

import com.space.mapper.SpaceMapper;
import com.space.mapper.UserMapper;
import com.space.model.entity.Space;
import com.space.model.entity.User;
import com.space.service.UserService;
import com.space.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;


public class UserServiceImpl implements UserService {

    // 使用事务确保用户注册和空间初始化的原子性
    @Override
    public boolean registerUser(User user) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            
            // 插入用户信息，此时会自动设置user对象的userId属性
            int result = userMapper.register(user);
            
            // 获取刚插入用户的ID
            Long userId = user.getUserId();

            // 添加个人空间
            Space space = new Space();
            space.setUserId(userId);
            space.setTotalSize(5*1024*1024L);
            space.setUsedSize(0L);

            boolean spaceResult = spaceMapper.addUserSpace(space);

            if (result > 0 && spaceResult ) {
                // 提交事务
                sqlSession.commit();
                return true;
            }
            sqlSession.rollback();
            return false;
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("用户注册失败：" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public User getUserById(Long userId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            return userMapper.selectUserById(userId);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public User login(String account, String password) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            // 实际应用中应进行密码加密比较
            User login = userMapper.login(account, password);
            //System.out.println(login);
            return login;
        } finally {
            sqlSession.close();
        }
    }
}