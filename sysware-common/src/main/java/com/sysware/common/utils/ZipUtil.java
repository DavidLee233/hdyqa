package com.sysware.common.utils;

import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ZipUtil {
    public static Map<String, String> processZipFile(File  file, String outputDir) {
    Map<String, String> xmlContentMap = new HashMap<>();

    // 确保输出目录存在
    File outputDirectory = new File(outputDir);
    if (!outputDirectory.exists()) {
        outputDirectory.mkdirs();
    }

    try (ZipFile zipFile = new ZipFile(file)) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        zipFile.stream().forEach(entry -> {
            String entryName = entry.getName();
            try (InputStream inputStream = zipFile.getInputStream(entry)) {
                if (entryName.toLowerCase().endsWith(".xml")) {
                    // 处理XML文件
                    Document document = builder.parse(inputStream);
                    document.getDocumentElement().normalize();
                    parseXmlElement(document.getDocumentElement(), "", xmlContentMap);
                    System.out.println("成功解析XML: " + entryName);
                } else if (entryName.toLowerCase().endsWith(".pdf")) {
                    // 处理PDF文件 - 保存到本地
                    savePdfToFile(inputStream, outputDir, entryName);
                    System.out.println("成功保存PDF: " + entryName);
                }
            } catch (Exception e) {
                System.err.println("处理条目失败: " + entryName + " - " + e.getMessage());
            }
        });
    } catch (Exception e) {
        System.err.println("处理ZIP文件失败: " + e.getMessage());
    }
    return xmlContentMap;
}

        public static void savePdfToFile(InputStream inputStream, String outputDir, String entryName) {
            String safeFileName = Paths.get(entryName).getFileName().toString(); // 防止路径遍历
            File outputFile = new File(outputDir, safeFileName);

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

        private static void parseXmlElement(Element element, String parentPath, Map<String, String> resultMap) {
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
                    parseXmlElement((Element) node, currentPath, resultMap);
                }
            }
        }

        /*public static void main(String[] args) {
            String zipPath = "D:\\sysware\\test\\test.zip";
            String outputDir = "D:\\sysware\\test\\file";

            Map<String, String> xmlData = processZipFile(zipPath, outputDir);

            System.out.println("\nXML解析结果:");
            xmlData.forEach((key, value) -> System.out.println(key + " = " + value));

            System.out.println("\nPDF文件已保存到目录: " + new File(outputDir).getAbsolutePath());
        }*/
}

