package com.sysware.qa.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class attachUtils {
    /**
     * @author lxd
     * @description: 文件支持类型
     * @param filename
     * @return boolean
     * @date 2025/7/26
     **/
    public static boolean isSupportedFileType(String filename) {
        String[] supportedTypes = {"doc", "docx", "xls", "xlsx", "ppt", "pptx"};
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.asList(supportedTypes).contains(extension);
    }

    /**
     * @author lxd
     * @description: 检查临时存储文件夹是否存在
     * @param tempDir
     * @return void
     * @date 2025/7/26
     **/
    public static void checkDir(String tempDir){
        File file = new File(tempDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * @author lxd
     * @description: 临时文件存储
     * @param file
     * @param tempDir
     * @return java.io.File
     * @date 2025/7/26
     **/
    public static File saveTempFile(MultipartFile file, String tempDir) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String tempFilePath = null;
        if (originalFilename != null) {
            tempFilePath = tempDir + "/" + System.currentTimeMillis() + "." + originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        File tempFile = null;
        if (tempFilePath != null) {
            tempFile = new File(tempFilePath);
            file.transferTo(tempFile);
        }
        return tempFile;
    }

    /**
     * @author lxd
     * @description: 使用LibreOffice转换文件格式
     * @param inputFile
     * @param tempDir
     * @return java.io.File
     * @date 2025/7/26
     **/
    public static File convertWithLibreOffice(File inputFile, String tempDir, String libreOfficePath)
            throws IOException, InterruptedException {

        // 标准化路径处理
        Path outputDir = Paths.get(tempDir).toAbsolutePath().normalize();
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }

        // 构造输出文件名（保留原文件名基础）
        String outputFilename = inputFile.getName().replaceFirst("\\.\\w+$", "") + ".pdf";
        Path outputPath = outputDir.resolve(outputFilename);

        // 构建转换命令
        ProcessBuilder pb = new ProcessBuilder(
                libreOfficePath,
                "--headless",
                "--convert-to", "pdf:writer_pdf_Export",
                "--outdir", outputDir.toString(),
                inputFile.getAbsolutePath()
        );

        // 重定向错误流以便调试
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);

        // 启动进程
        Process process = pb.start();

        // 捕获输出日志
        StringBuilder log = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.append(line).append("\n");
            }
        }

        // 等待完成
        int exitCode = process.waitFor();

        // 验证输出文件
        if (exitCode == 0) {
            if (Files.exists(outputPath)) {
                return outputPath.toFile();
            }
            // 尝试查找可能的输出文件（LibreOffice有时会修改命名）
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(outputDir, "*.pdf")) {
                for (Path pdf : stream) {
                    if (Files.isRegularFile(pdf)) {
                        return pdf.toFile();
                    }
                }
            }
        }
        throw new IOException(String.format(
                "转换失败 (Exit code %d)\n命令: %s\n输出: %s",
                exitCode,
                String.join(" ", pb.command()),
                log.toString()
        ));
    }

    /**
     * @author lxd
     * @description: 清理临时文件
     * @param files
     * @return void
     * @date 2025/7/26
     **/
    public static void cleanTempFiles(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
