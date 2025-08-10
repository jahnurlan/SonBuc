package com.example.adminms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AdminMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminMsApplication.class, args);
    }

}
