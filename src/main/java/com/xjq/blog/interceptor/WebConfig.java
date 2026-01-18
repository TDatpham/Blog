package com.xjq.blog.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer { // WebMvcConfigurerAdapter 在Spring5.0已被废弃

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Spring Security handles authentication
        // registry.addInterceptor(new LoginInterceptor())
        // .addPathPatterns("/admin/**")
        // .excludePathPatterns("/admin")
        // .excludePathPatterns("/admin/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/chat/ask").allowedOrigins("*");
    }
}
