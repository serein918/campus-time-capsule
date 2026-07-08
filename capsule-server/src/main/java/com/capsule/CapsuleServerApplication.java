package com.capsule;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

@MapperScan("com.capsule.mapper")

public class CapsuleServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(CapsuleServerApplication.class,args);

    }

}