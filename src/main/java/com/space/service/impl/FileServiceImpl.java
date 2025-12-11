package com.space.service.impl;

import com.space.mapper.FileMapper;
import com.space.mapper.SpaceMapper;
import com.space.model.entity.Files;
import com.space.service.FileService;
import com.space.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

public class FileServiceImpl implements FileService {
    
    @Override
    public boolean saveFile(Files file) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            
            // 先更新用户空间使用量
            boolean spaceUpdated = spaceMapper.updateUsedSize(file.getUserId(), file.getFileSize());
            
            if (!spaceUpdated) {
                sqlSession.rollback();
                return false;
            }
            
            // 保存文件信息
            boolean result = fileMapper.save(file);
            
            if (result) {
                sqlSession.commit();
                return true;
            }
            sqlSession.rollback();
            return false;
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("文件保存失败：" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }
}