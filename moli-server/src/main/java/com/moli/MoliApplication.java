package com.moli;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.moli.*.mapper")
public class MoliApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoliApplication.class, args);
    }

}
