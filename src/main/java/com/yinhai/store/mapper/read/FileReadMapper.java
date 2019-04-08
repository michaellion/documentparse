package com.yinhai.store.mapper.read;

import com.yinhai.store.entity.FileDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-08 08:19
 **/
@Mapper
public interface FileReadMapper {

    List<FileDetails> listFiles();

    FileDetails getFileById(Integer id);

    List<FileDetails> getFileByUploader(String uploader);

    List<FileDetails> findEnableFilesLike();
}
