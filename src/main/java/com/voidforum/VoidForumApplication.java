package com.voidforum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing // <--- ¡Agregá esto!
public class VoidForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(VoidForumApplication.class, args);
    }
}