package com.space.service.impl;

import com.space.mapper.FileMapper;
import com.space.mapper.SpaceMapper;
import com.space.service.SpaceService;
import com.space.util.MyBatisUtil;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSession;

public class SpaceServiceImpl implements SpaceService {
    @Override
    public Long findUsedSizeById(Long userId) {
        try (SqlSession sqlSession = MyBatisUtil.getSession()) {
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            return spaceMapper.selectUsedSize(userId);
        } catch (Exception e) {
            throw new RuntimeException("fail to get used size");
        }
    }

    @Override
    public Boolean updateUsedSize(long userId,long l) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try{
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            boolean result = spaceMapper.updateUsedSize(userId,l);
            sqlSession.commit();
            return result;
        }catch (Exception e){
            sqlSession.rollback();
            throw new RuntimeException("fail to unfreeze file");
        }finally {
            sqlSession.close();
        }
    }
}
