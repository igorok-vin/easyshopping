package com.easyshopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
@EntityScan({"com.easyshopping.common.entity", "com.easyshopping"})
public class EasyShoppingFrontEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyShoppingFrontEndApplication.class, args);
    }

}
