package com.yinhai.store.controller;


import com.yinhai.store.entity.FileDetails;
import com.yinhai.store.service.write.FileWriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @description: upload the file
 * @author: Mr.Li
 * @create: 2019-04-01 11:40
 **/

@Controller
public class FileController {
    @Value("$(file.uploaddir)")
    private String UPLOADDIR;

    @Autowired
    FileWriteService fileWriteService;
    /*
    @Autowired
    FileReadService fileReadService;*/

    @RequestMapping("/index")
    String file(){
        return "/upload";
    }
    @RequestMapping("/multifile")
        public String multifile(){
        return "/multifile";
    }


    /**
     *  单个文件上传
     * @param multipartFile
     * @return
     */
    @RequestMapping("/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam(name = "file")MultipartFile multipartFile){
        FileDetails fileDetails = null;
        //上传的文件是否为空
        if(!multipartFile.isEmpty()){
            String path = UPLOADDIR+multipartFile.getOriginalFilename();
            File file = new File(path);
             if(file.exists()){
                 return "file already exists";
            }
            try(BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file))){
                byte [] bytes = multipartFile.getBytes();
                os.write(bytes);

            }catch (IOException e){
                    e.fillInStackTrace();
            }
            try {
                fileDetails = new FileDetails();
                fileDetails.setUploader("jian");
                fileDetails.setFilePath(path);
                fileDetails.setFileSize(multipartFile.getSize() + "B");
                fileDetails.setFileType(Files.probeContentType(Paths.get(path)));
                fileDetails.setUploadDate(new Date());
                fileDetails.setEnable("true");

            }catch (Exception e){
                e.fillInStackTrace();
            }

            fileWriteService.addFile(fileDetails);
            return "successful";
        }else{
            return "upload fail"+" file is empty";
        }
    }

    /**
     *批量上传
     */

    @RequestMapping("/batch/upload")
    @ResponseBody
    public String handleFileUpload(@RequestParam(name = "file")MultipartFile[] multipartFile) throws Exception{
        //Arrays.asList(multipartFile).stream().forEach(m -> handleFileUpload(multipartFile));
        if(multipartFile.length>0) {
            List<MultipartFile> list = Arrays.asList(multipartFile);
            FileDetails fileDetails = null;
            File files = null;
            String path = "";
            List<FileDetails> result = new ArrayList<>();
            /**
             * 批量上传到零时目录，然后同一存储和
             */
            try {
                for (MultipartFile file : list) {
                    fileDetails = new FileDetails();
                    path = UPLOADDIR + file.getOriginalFilename();
                    files = new File(path);
                    //上传到制定目录
                    byte [] bytes = file.getBytes();
                    new BufferedOutputStream(new FileOutputStream(files)).write(bytes);
                    //构造filedetails对象
                    fileDetails.setUploader("jian");
                    fileDetails.setUploadDate(new Date());
                    fileDetails.setFileType(Files.probeContentType(Paths.get(path)));
                    fileDetails.setFileSize(file.getSize() + "B");
                    fileDetails.setFilePath(path);
                    fileDetails.setEnable("true");
                    result.add(fileDetails);
                }
            }catch (Exception e){
                e.fillInStackTrace();
            }
            //批量上传文件
            fileWriteService.addBatchFiles(result);

            //然后进行批量事务处理
            fileWriteService.getDocFiles(result);
            fileWriteService.saveBatch(result);
        }
        return "success";
    }


}
