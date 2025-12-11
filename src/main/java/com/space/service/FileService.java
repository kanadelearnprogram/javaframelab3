package com.space.service;

import com.space.model.entity.Files;

import java.util.List;

public interface FileService {
    boolean saveFile(Files file);
    List<Files> listFile(Long userId);
    boolean incrementDownloadCount(Long fileId);
    Integer getDownloadCount(Long fileId);
}