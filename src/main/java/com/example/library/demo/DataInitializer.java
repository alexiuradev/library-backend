package com.example.library.demo;

import com.example.library.auth.AuthService;
import com.example.library.auth.dto.AuthRegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(AuthService authService) {
        return args -> {
            try {
                AuthRegisterRequest admin = new AuthRegisterRequest();
                admin.setEmail("admin@test.com");
                admin.setPassword("secret12");
                authService.registerMember(admin);

                AuthRegisterRequest author = new AuthRegisterRequest();
                author.setEmail("author@test.com");
                author.setPassword("secret12");
                authService.registerMember(author);

                System.out.println("Test users created");
            } catch (Exception ignored) {
                // already exists
            }
        };
    }

}
