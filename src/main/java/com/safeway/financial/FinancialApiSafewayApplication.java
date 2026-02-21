package com.safeway.financial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FinancialApiSafewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialApiSafewayApplication.class, args);
    }

}
