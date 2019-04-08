package com.yinhai.store.entity;

import lombok.Data;

import java.util.Date;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-08 08:17
 **/
public @Data class FileDetails {
    private long id;
    /**
     * 上传人
     */
    private String uploader;
    /**
     * 上传日期
     */
    private Date uploadDate;
    /**
     * 文件的大小
     */
    private String fileSize;
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 标志位
     */
    private String enable;
}
