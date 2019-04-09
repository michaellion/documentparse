package com.yinhai.parse;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.extractor.POIXMLPropertiesTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ParseDocument {
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

    public String readFile(String path){
        String line ="";
        StringBuffer result = new StringBuffer();
        try (FileReader reader = new FileReader(path);
             BufferedReader br = new BufferedReader(reader)
        ) {
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


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
                WordExtractor doc = new WordExtractor(new FileInputStream(new File(path)));
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
