package com.sysware.common.utils;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import java.io.InputStream;

public class DataHandlerUtil {


    public static DataHandler inputStreamToDataHandler(InputStream inputStream) {
        // 创建一个数据源
        DataSource dataSource = new DataSource() {
            @Override
            public InputStream getInputStream() {
                return inputStream; // 返回输入流
            }

            @Override
            public String getContentType() {
                return "application/octet-stream"; // 默认内容类型
            }

            @Override
            public String getName() {
                return "InputStreamDataSource"; // 数据源名称
            }

            @Override
            public java.io.OutputStream getOutputStream() {
                throw new UnsupportedOperationException("Not supported");
            }
        };

        // 创建并返回DataHandler对象
        return new DataHandler(dataSource);
    }
}
