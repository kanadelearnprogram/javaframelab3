package com.space.service;

import com.space.model.entity.Space;
import org.springframework.stereotype.Service;


public interface SpaceService {

    Long findUsedSizeById(Long userId);

    Boolean updateUsedSize(long userId,long l);

    Long findTotalSize(Long userId);

    Boolean updateTotalSize(Long userId, Long newTotalSize);
    
    // 新增空间扩容相关方法
    Boolean submitExpansionApplication(Long userId, Long applySize);
    
    Space findByUserId(Long userId);
}