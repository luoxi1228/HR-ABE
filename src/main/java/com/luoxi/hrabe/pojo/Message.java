package com.luoxi.hrabe.pojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private Integer fileId;
    private String userId;
    private String encKey;
    private String fileName;
    private String fileType;
    private String fileSize;
    private String filePath;
    private LocalDateTime time;
    private String policy;
}

