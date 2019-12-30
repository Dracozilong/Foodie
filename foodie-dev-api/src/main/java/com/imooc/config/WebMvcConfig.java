package com.imooc.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:D:\\faces\\")//映射本地静态资源
                .addResourceLocations("classpath:/META-INF/resources/"); //映射swagger2

//        registry.addResourceHandler("/**")
//                .addResourceLocations("file:/Users/zhangqiushi/DEV/WorkSpace/")//映射本地静态资源
//                .addResourceLocations("classpath:/META-INF/resources/"); //映射swagger2
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){

     return  builder.build();

    }
}
