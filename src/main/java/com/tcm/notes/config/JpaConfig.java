package com.tcm.notes.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA配置类
 * 显式指定实体类和仓库接口的包路径，确保Spring能正确扫描到它们
 */
@Configuration
@EntityScan(basePackages = {"com.tcm.notes.entity"})
@EnableJpaRepositories(basePackages = {"com.tcm.notes.repository"})
public class JpaConfig {
    // 配置类不需要额外的方法，注解已经完成了配置工作
}