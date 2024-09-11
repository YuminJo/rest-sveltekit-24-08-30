package com.ll.rsv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RsvApplication {

    public static void main(String[] args) {
        SpringApplication.run(RsvApplication.class, args);
    }

}
