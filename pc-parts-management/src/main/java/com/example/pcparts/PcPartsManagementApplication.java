package com.example.pcparts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.pcparts.mapper")
@SpringBootApplication
public class PcPartsManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcPartsManagementApplication.class, args);
    }
}
