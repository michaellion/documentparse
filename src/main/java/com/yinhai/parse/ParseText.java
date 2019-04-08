package com.yinhai.parse;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-02 15:18
 **/
@Service
public class ParseText {



    public String readFile(String path){
        String line ="";
        StringBuffer result = new StringBuffer();
        try (FileReader reader = new FileReader(path);
             BufferedReader br = new BufferedReader(reader)
        ) {

            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
             //
               result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }




}
