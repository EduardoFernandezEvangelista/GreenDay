package br.com.senai.greenday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GreenDayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenDayApplication.class, args);
    }
}
