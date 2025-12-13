package com.space.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files {
    private Long id;
    private Long userId;
    private String fileName;
    private String filePath;
    private Long fileSize;
    private String fileType;
    private LocalDateTime uploadTime;
    private Integer status;
    private Integer isTop;
    private Integer downloadCount;
    private Integer review;
    private Integer isDelete;
}