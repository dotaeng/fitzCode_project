package kr.co.fitzcode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FitzCodeApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitzCodeApplication.class, args);
    }
}