package com.yinhai.parse;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @description: parse the word docuement
 * @author: Mr.Li
 * @create: 2019-04-01 10:04
 **/
@Service
public class ParseWord {


    public String parseDocx(String path){

        try (

            XWPFDocument docx = new XWPFDocument (new FileInputStream(new File(path)));
            XWPFWordExtractor extractor = new XWPFWordExtractor(docx)
        ){
            return extractor.getText();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    public String parseDoc(String path){
        try (
            WordExtractor  doc = new WordExtractor(new FileInputStream(new File(path)));
        ){
            return doc.getText();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
}
