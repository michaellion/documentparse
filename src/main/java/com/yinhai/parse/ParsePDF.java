package com.yinhai.parse;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-02 15:38
 **/
@Service
public class ParsePDF {

    public  String readPdf(String path){
        String pageContent = "";
        try {
            PdfReader reader = new PdfReader(path);
            int pageNum = reader.getNumberOfPages();
            for(int i=1;i<=pageNum;i++){
                /**
                 * 读取第i页的文档内容
                 * */
                pageContent += PdfTextExtractor.getTextFromPage(reader, i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageContent;
    }


}
