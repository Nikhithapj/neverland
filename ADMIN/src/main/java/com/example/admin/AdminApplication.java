package com.example.admin;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@SpringBootApplication(scanBasePackages = {"com.example.library.*", "com.example.admin.*"})
@EnableJpaRepositories(value = "com.example.library.repository")
@EntityScan(value = "com.example.library.model")
public class
AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
//    @Bean
//    public JavaMailSender javaMailSender(){return new JavaMailSenderImpl();
//    }


}