package com.space.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilesVO {
        private Long id;
        private Long userId;
        private String fileName;
        private String filePath;
        private Long fileSize;
        private LocalDateTime uploadTime;
        private String status;
        private Integer isTop;
        private Integer downloadCount;
        private Integer review;
        private Integer isDelete;
}
