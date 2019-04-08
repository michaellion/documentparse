package com.yinhai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.yinhai.store.*")
public class DocumentparseApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocumentparseApplication.class, args);
    }

}
