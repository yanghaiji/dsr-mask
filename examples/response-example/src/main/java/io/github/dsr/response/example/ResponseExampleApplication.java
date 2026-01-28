package io.github.dsr.response.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "io.github.dsr.**")
public class ResponseExampleApplication {


    public static void main(String[] args) {
        SpringApplication.run(ResponseExampleApplication.class, args);
    }

}
