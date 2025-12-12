package com.space.service;

import org.springframework.stereotype.Service;


public interface SpaceService {

    Long findUsedSizeById(Long userId);

    Boolean updateUsedSize(long userId,long l);
}
