package com.space.service;

import org.springframework.stereotype.Service;


public interface SpaceService {

    Long findUsedSizeById(Long userId);

    Boolean updateUsedSize(long userId,long l);

    Long findTotalSize(Long userId);

    Boolean updateTotalSize(Long userId, Long newTotalSize);
}
