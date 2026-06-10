package com.financebuddy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.financebuddy.repository")
public class FinanceBuddyApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinanceBuddyApplication.class, args);
    }
}