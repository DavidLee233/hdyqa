package com.sysware.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SyswareMD5Util {
        private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

        /**
         * 转换字节数组为16进制字串
         *
         * @param b 字节数组
         * @return 16进制字串
         */

        public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

        private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

        /**
         * 字符串转成MD5格式
         *
         * @param origin 原始字符串
         * @return
         */
        public static String MD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString.getBytes()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultString;
    }

    /**
     * 生成文件的MD5校验码
     * @param fis 文件输入流
     * @return MD5校验码字符串
     * @throws IOException 如果文件读取失败
     * @throws NoSuchAlgorithmException 如果MD5算法不可用
     */
    public static String generateFileMD5(FileInputStream fis) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) != -1) {
            md.update(buffer, 0, length);
        }
        byte[] digest = md.digest();
        return bytesToHex(digest); // 将字节数组转换成16进制字符串
    }

    /**
     * 将字节数组转换为16进制字符串
     * @param bytes 字节数组
     * @return 16进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
