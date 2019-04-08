package com.yinhai.parse;

import com.yinhai.store.entity.FileDetails;

/**
 * @description:
 * @author: Mr.Li
 * @create: 2019-04-09 04:01
 **/
public class ParseInvoke {

    public String invoke(FileDetails fileDetails){
        String content = "";
        switch (fileDetails.getFileType().toLowerCase()){
            case "pdf":
                new ParsePDF().readPdf(fileDetails.getFilePath());
                break;
            case "txt":
                new ParseText().readFile(fileDetails.getFilePath());
                break;
            case "doc":
                new ParseWord().parseDoc(fileDetails.getFilePath());
                break;
            case "docx":
                new ParseWord().parseDocx(fileDetails.getFilePath());
                break;
            case "ppt":
                new ParseSlide().parsePPT(fileDetails.getFilePath());
                break;
            case "pptx":
                new ParseSlide().parsePPTX(fileDetails.getFilePath());
                break;
            case "xls":
                new PaeseExcel().parseXLS(fileDetails.getFilePath());
                break;
            case "xlsx":
                new PaeseExcel().parseXLSX(fileDetails.getFilePath());
                break;

            default: return "文件类型不存在";
        }
        return content;
    }
}
