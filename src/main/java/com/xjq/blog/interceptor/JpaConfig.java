package com.xjq.blog.interceptor;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.xjq.blog.repository")
@EntityScan(basePackages = "com.xjq.blog.model")
public class JpaConfig {
    // Các cấu hình khác nếu cần
}

