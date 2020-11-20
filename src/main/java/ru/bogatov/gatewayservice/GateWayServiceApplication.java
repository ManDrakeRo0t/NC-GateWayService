package ru.bogatov.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class GateWayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GateWayServiceApplication.class, args);
    }

}
