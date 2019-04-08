package com.yinhai;

import com.yinhai.store.entity.FileDetails;
import com.yinhai.store.service.read.FileReaderService;
import com.yinhai.store.service.write.FileWriteService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentparseApplicationTests {
    @Autowired
    FileReaderService fileReaderService;
    @Autowired
    FileWriteService fileWriteService;


   @Test
    public void listAlltest() {
       fileReaderService.listFiles().stream().forEach(System.out::println);
   }

   @Test
   public void addDetailTest(){
       FileDetails fileDetails = new FileDetails();
       fileDetails.setUploadDate(new Date());
       fileDetails.setUploader("jian");
       fileDetails.setEnable("false");
       fileDetails.setFileSize("1.3M");
       fileDetails.setFileType("pptx");
       fileDetails.setFilePath("C:/");

       fileWriteService.addFile(fileDetails);
   }




   @Test
   @Transactional
   public void addBatchFiles(){
       FileDetails fileDetails;
       List<FileDetails> list = new ArrayList();
       for (int i = 0; i <10 ; i++) {
           fileDetails = new FileDetails();
           fileDetails.setUploadDate(new Date());
           fileDetails.setUploader("jian"+i);
           fileDetails.setEnable("false");
           fileDetails.setFileSize("1M");
           fileDetails.setFileType("pptx");
           fileDetails.setFilePath("C:/");
           list.add(fileDetails);
       }
       fileWriteService.addBatchFiles(list);
   }

    @Test
   public void getOne(){
        System.out.println(fileReaderService.getFileById(2));
    }

   @Test
   public void getFilesByUploader(){
       fileReaderService.getFilesByName("jian").stream().forEach(System.out::println);
   }

}
