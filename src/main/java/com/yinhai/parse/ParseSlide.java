package com.yinhai.parse;

import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

/**
 * @description: parseSlides
 * @author: Mr.Li
 * @create: 2019-04-01 10:04
 **/
@Service
public class ParseSlide {


    public String parsePPT(String path){

       try(
        HSLFSlideShow hslfSlideShow = new HSLFSlideShow(new FileInputStream(new File(path)));
        SlideShowExtractor extractor = new SlideShowExtractor(hslfSlideShow)
        ) {
           return extractor.getText();
       }catch (Exception e){
           e.fillInStackTrace();
       }
       return "";
    }

    public String parsePPTX(String path){
        try(
        XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(new File(path)));
        SlideShowExtractor pptExtrator = new SlideShowExtractor(ppt)
        ){
            return pptExtrator.getText();
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return "";
    }

}
