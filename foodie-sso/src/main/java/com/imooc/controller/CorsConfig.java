package com.imooc.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter() {
        //1.添加cors配置信息
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:8080");
        configuration.addAllowedOrigin("http://shop.zqiush.com:8080");
        configuration.addAllowedOrigin("http://center.zqiush.com:8080");
        configuration.addAllowedOrigin("http://shop.zqiush.com");
        configuration.addAllowedOrigin("http://center.zqiush.com");
        configuration.addAllowedOrigin("http://www.mtv.com");
        configuration.addAllowedOrigin("http://www.mtv.com:8080");
        configuration.addAllowedOrigin("http://www.music.com");
        configuration.addAllowedOrigin("http://www.music.com:8080");

        //configuration.addAllowedOrigin("*");
        //2.是否发送cookie请求
        configuration.setAllowCredentials(true);
        //设置允许请求的方式
        configuration.addAllowedMethod("*");
        //设置允许的header
        configuration.addAllowedHeader("*");
        //2. 为url添加映射路径
        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", configuration);

        //重新定义corsSource返回
        return new CorsFilter(corsSource);
    }
}
