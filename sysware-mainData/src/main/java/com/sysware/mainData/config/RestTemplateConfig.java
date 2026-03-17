package com.sysware.mainData.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
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
