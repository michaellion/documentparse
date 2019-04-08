package com.yinhai.store.service.read;

import com.yinhai.store.entity.FileDetails;
import com.yinhai.store.mapper.read.FileReadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-08 08:33
 **/
@Service
public class FileReaderService {

    @Autowired
    FileReadMapper fileReadMapper;


    public List<FileDetails> listFiles(){
       return fileReadMapper.listFiles();
    }

    public FileDetails getFileById(Integer id){
        return fileReadMapper.getFileById(id);
    }

    public List<FileDetails> getFilesByName(String uploader){
        return fileReadMapper.getFileByUploader(uploader);
    }
}
