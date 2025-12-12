package com.space.service;

import com.space.model.entity.Files;

import java.util.List;

public interface FileService {
    boolean saveFile(Files file);
    List<Files> listFile(Long userId);
    List<Files> listFileWithPagination(Long userId, int pageNum, int pageSize);
    boolean incrementDownloadCount(Long fileId);
    Integer getDownloadCount(Long fileId);
    Boolean freezeFile(Long fileId);
    Boolean unfreezeFile(Long fileId);
    Boolean deleteFile(Long fileId);

    String findPathById(Long fileId);

    Long findSizeById(Long fileId);

    String findNameById(Long fileId);
}