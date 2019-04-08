package com.yinhai.parse;

import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.File;
import java.io.FileInputStream;

/**
 * @description: Paese the Excel
 * @author: Mr.Li
 * @create: 2019-04-01 10:05
 **/
@Service
public class PaeseExcel {
    public String parseXLSX(String path){
        try(
            OPCPackage pkg = OPCPackage.open((new File(path)).toString());
            XSSFExcelExtractor ext = new XSSFExcelExtractor(new XSSFWorkbook(pkg));
            ){
                POIXMLPropertiesTextExtractor textExt = ext.getMetadataTextExtractor();
                return textExt.getText();
        }catch (Exception e){e.fillInStackTrace();}
        return "";
    }
    public String parseXLS(String path){
        try(
        POIFSFileSystem poifsFileSystem = new POIFSFileSystem(new FileInputStream(path));
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(poifsFileSystem);
        ) {
            ExcelExtractor excelExtractor = new org.apache.poi.hssf.extractor.ExcelExtractor(hssfWorkbook);
            return excelExtractor.getText();
        }catch (Exception e){
            e.fillInStackTrace();
        }
        return "";
    }
}
