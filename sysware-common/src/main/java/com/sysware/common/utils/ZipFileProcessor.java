package com.sysware.common.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ZipFileProcessor {

    private static  String PROP = "prop";
    private static  String DOC_SOURCE = "DocSource";
    private static  String ORIGINAL_FILE_INFOS = "OriginalFileInfos";

    public  Map<String, String> processZipFile(InputStream zipInputStream, String pdfStoragePath,String fileName) throws Exception {

        Map<String, String> xmlContentMap = new LinkedHashMap<>();
        // 创建临时文件
        //File tempFile =  new File(pdfStoragePath+"/"+fileName);
        File tempFile = File.createTempFile("upload_", ".zip");
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            // 使用Java 8的transferTo方法替代方案
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }

        // 处理ZIP文件
        try (ZipFile zipFile = new ZipFile(tempFile)) {
            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
            
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                InputStream entryStream = zipFile.getInputStream(entry);
                
                if (entry.getName().toLowerCase().endsWith(".xml")) {
                    xmlContentMap =   processXmlFile(entryStream,xmlContentMap);
                } else {
                    saveFile(entryStream, pdfStoragePath, entry.getName());
                }
                entryStream.close();
            }
        } finally {
            Files.deleteIfExists(tempFile.toPath());
            //tempFile.delete();
        }

        return xmlContentMap;
    }

    private Map processXmlFile(InputStream xmlStream,Map<String,String> xmlContentMap) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document document = builder.parse(xmlStream);
        document.getDocumentElement().normalize();
        parseXmlElement(document.getDocumentElement(), "", xmlContentMap);
        return xmlContentMap;
    }

    private  void parseXmlElement(Element element, String parentPath, Map<String, String> resultMap) {
        String currentPath = parentPath.isEmpty()
                ? element.getNodeName()
                : parentPath + "." + element.getNodeName();


        // 处理文本内容
        NodeList textNodes = element.getChildNodes();
        for (int i = 0; i < textNodes.getLength(); i++) {
            Node node = textNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE && !node.getTextContent().trim().isEmpty()) {
                resultMap.put(currentPath, node.getTextContent().trim());
                break;
            }
        }

        // 处理属性
        if (element.hasAttributes()) {
            for (int i = 0; i < element.getAttributes().getLength(); i++) {
                Node attr = element.getAttributes().item(i);
                resultMap.put(currentPath + "@" + attr.getNodeName(), attr.getNodeValue());
            }
        }

        // 递归处理子元素
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {


                if(PROP.equals(node.getNodeName())){
                    saveXmlProp((Element)node,"");
                }else if(DOC_SOURCE.equals(node.getNodeName())){
                    saveXmlDcoSource((Element)node,"");
                }else if (ORIGINAL_FILE_INFOS.equals(node.getNodeName())){
                    saveXmlFileInfo((Element)node,"");
                }

                parseXmlElement((Element) node, currentPath+i, resultMap);
            }
        }
    }

    private  void saveXmlProp(Element element,String docId){
        NodeList textNodes = element.getChildNodes();
        for (int i = 0; i < textNodes.getLength(); i++) {
            Node node = textNodes.item(i);
            if (node.getNodeType() == Node.TEXT_NODE && !node.getTextContent().trim().isEmpty()) {

                break;
            }
        }
    }
    private  void saveXmlDcoSource(Element element,String docId){

    }
    private  void saveXmlFileInfo(Element element,String docId){

    }


    public  void saveFile(InputStream inputStream, String outputDir, String entryName) {
        String safeFileName = Paths.get(entryName).getFileName().toString(); // 防止路径遍历
        File outputFile = new File(outputDir, System.currentTimeMillis()+"_"+safeFileName);

        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            System.err.println("保存PDF文件失败: " + safeFileName + " - " + e.getMessage());
        }
    }

}