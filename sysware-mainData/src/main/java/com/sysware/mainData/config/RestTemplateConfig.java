package com.sysware.mainData.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @project npic
 * @description RestTemplateConfig配置类，负责注册主数据模块配置相关运行组件与参数。
 * @author DavidLee233
 * @date 2026/3/20
 */
@Configuration
public class RestTemplateConfig {
    /**
     * @description 执行restTemplate方法，完成主数据模块配置相关业务处理。
     * @params 无
     *
      * @return RestTemplate 类型业务结果。
     * @author DavidLee233
     * @date 2026/3/20
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 设置连接超时时间（单位：毫秒）
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(30000);

        // 如果需要代理，可以在这里配置
        // factory.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy-host", 8080)));

        return new RestTemplate(factory);
    }
}