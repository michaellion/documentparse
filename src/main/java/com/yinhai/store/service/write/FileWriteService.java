package com.yinhai.store.service.write;

import com.yinhai.client.ElasticSearchClient;
import com.yinhai.parse.ParseInvoke;
import com.yinhai.search.entity.DocDetail;
import com.yinhai.store.entity.FileDetails;
import com.yinhai.store.mapper.write.FileWriteMapper;
import com.yinhai.store.service.impl.DocPersistenceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-08 08:33
 **/
@Service
public class FileWriteService {
    @Autowired
    FileWriteMapper fileWriteMapper;

    @Autowired
    DocPersistenceServiceImpl docService;

    public void addFile(FileDetails fileDetails){
        fileWriteMapper.addFile(fileDetails);
    }

    public void addBatchFiles(List<FileDetails> list){
        fileWriteMapper.addBatchFiles(list);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveBatch(List<FileDetails> list){
        //ES batch
        fileWriteMapper.addBatchFiles(list);
        docService.save(getDocFiles(list));
    }



    public List<DocDetail> getDocFiles(List<FileDetails> list){
            DocDetail docDetail;
            List<DocDetail> result = new ArrayList<>();
            for(FileDetails files : list){
                String content = new ParseInvoke().invoke(files);
                docDetail = new DocDetail();
                docDetail.setAuthor(files.getUploader());
                docDetail.setContent(content);
                docDetail.setFileType(files.getFileType());
                docDetail.setUploadDate(files.getUploadDate().getTime());
                result.add(docDetail);
            }
            return result;
    }

}
