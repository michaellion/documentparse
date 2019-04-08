package com.yinhai.store.mapper.write;

import com.yinhai.store.entity.FileDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-08 08:19
 **/
@Mapper
public interface FileWriteMapper {

    /**
     *
     * @param param
     */
     void addFile(FileDetails param);

     void addBatchFiles(List<FileDetails> list);


}
