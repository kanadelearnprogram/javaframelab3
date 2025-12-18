package com.space.service.impl;

import com.space.mapper.FileMapper;
import com.space.mapper.SpaceMapper;
import com.space.model.Paths;
import com.space.model.entity.Files;
import com.space.service.FileService;
import com.space.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Files> listFile(Long userId) {
        SqlSession sqlSession = MyBatisUtil.getSession();

        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);

        // 保存文件信息
        List<Files> list = fileMapper.findByUserId(userId);
        
        sqlSession.close();

        return list;
    }
    
    @Override
    public List<Files> listFileWithPagination(Long userId, int pageNum, int pageSize) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        
        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
        
        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;
        
        // 获取分页数据
        List<Files> list = fileMapper.findByUserIdWithPagination(userId, offset, pageSize);
        
        sqlSession.close();
        
        return list;
    }
    
    @Override
    public boolean incrementDownloadCount(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            boolean result = fileMapper.incrementDownloadCount(fileId);
            sqlSession.commit();
            return result;
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("更新下载次数失败：" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }
    
    @Override
    public Integer getDownloadCount(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.getDownloadCount(fileId);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Boolean freezeFile(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try{
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            boolean result = fileMapper.freezeFile(fileId);
            sqlSession.commit();
            return result;
        }catch (Exception e){
            sqlSession.rollback();
            throw new RuntimeException("fail to freeze file");
        }finally {
            sqlSession.close();
        }
    }

    @Override
    public Boolean unfreezeFile(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try{
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            boolean result = fileMapper.unfreezeFile(fileId);
            sqlSession.commit();
            return result;
        }catch (Exception e){
            sqlSession.rollback();
            throw new RuntimeException("fail to unfreeze file");
        }finally {
            sqlSession.close();
        }
    }

    @Override
    public Boolean deleteFile(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try{
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            boolean result = fileMapper.deleteFile(fileId);
            sqlSession.commit();
            return result;
        }catch (Exception e){
            sqlSession.rollback();
            throw new RuntimeException("fail to delete file");
        }finally {
            sqlSession.close();
        }
    }

    @Override
    public String findPathById(Long fileId) {
        try (SqlSession sqlSession = MyBatisUtil.getSession()) {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findPathById(fileId);
        } catch (Exception e) {
            throw new RuntimeException("fail to find path by id");
        }
    }

    @Override
    public Long findSizeById(Long fileId) {
        try (SqlSession sqlSession = MyBatisUtil.getSession()) {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findSizeById(fileId);
        } catch (Exception e) {
            throw new RuntimeException("fail to find size by id");
        }
    }

    @Override
    public String findNameById(Long fileId) {
        try (SqlSession sqlSession = MyBatisUtil.getSession()) {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findNameById(fileId);
        } catch (Exception e) {
            throw new RuntimeException("fail to find name by id");
        }
    }
    
    @Override
    public Files findById(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findById(fileId.intValue());
        } finally {
            sqlSession.close();
        }
    }
    
    @Override
    public List<Files> findAll() {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findAll();
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public Boolean pinTop(Long fileId, Integer state) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try{
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            boolean result = fileMapper.pinTop(fileId,state);
            sqlSession.commit();
            return result;
        }catch (Exception e){
            sqlSession.rollback();
            throw new RuntimeException("fail to delete file");
        }finally {
            sqlSession.close();
        }
    }

    @Override
    public Boolean calPinTop(Long fileId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try{
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            boolean result = fileMapper.calPinTop(fileId);
            sqlSession.commit();
            return result;
        }catch (Exception e){
            sqlSession.rollback();
            throw new RuntimeException("fail to delete file");
        }finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Files> listTopFiles(Long userId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findTopFiles(userId);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<Files> listTopFilesAdmin(Long userId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findTopFilesByAdmin(userId);
        } finally {
            sqlSession.close();
        }
    }

    @Override
    public List<String> listImg(Long userId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            List<Paths> allImgPath = fileMapper.findAllImgPath(userId);
            List<String> list = allImgPath.stream()
                    .map(paths -> String.valueOf(paths.getId()))
                    .toList();
            return list;
        } finally {
            sqlSession.close();
        }
    }
    
    @Override
    public List<Files> listPendingFiles() {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            return fileMapper.findPendingFiles();
        } finally {
            sqlSession.close();
        }
    }
    
    @Override
    public boolean approveFile(Long fileId, Long adminId) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            // 更新文件状态为已审核
            boolean result = fileMapper.approveFile(fileId);
            
            if (result) {
                // 记录审核日志
                fileMapper.insertAuditRecord(fileId, "file", adminId, "通过", "审核通过");
                sqlSession.commit();
            } else {
                sqlSession.rollback();
            }
            
            return result;
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("文件审核失败：" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }
    
    @Override
    public boolean rejectFile(Long fileId, Long adminId, String reason) {
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            // 更新文件状态为已驳回
            boolean result = fileMapper.rejectFile(fileId);
            
            if (result) {
                // 记录审核日志
                String auditReason = (reason != null && !reason.isEmpty()) ? reason : "审核驳回";
                fileMapper.insertAuditRecord(fileId, "file", adminId, "驳回", auditReason);
                sqlSession.commit();
            } else {
                sqlSession.rollback();
            }
            
            return result;
        } catch (Exception e) {
            sqlSession.rollback();
            throw new RuntimeException("文件驳回失败：" + e.getMessage());
        } finally {
            sqlSession.close();
        }
    }
}