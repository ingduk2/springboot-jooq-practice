package com.hello.jooq.mybatis.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.hello.jooq.mybatis.mapper")
public class MyBatisConfig {
}
