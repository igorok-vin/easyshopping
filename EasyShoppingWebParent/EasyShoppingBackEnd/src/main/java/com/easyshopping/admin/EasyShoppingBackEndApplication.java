package com.easyshopping.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.easyshopping.common.entity", "com.easyshopping.admin.user"})
public class EasyShoppingBackEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyShoppingBackEndApplication.class, args);
    }

}
