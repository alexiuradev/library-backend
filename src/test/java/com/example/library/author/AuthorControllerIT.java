package com.example.library.author;

import com.example.library.auth.dto.AuthResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthorControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndGetAuthor_asAuthenticatedUser() throws Exception {
        // Register user (MEMBER by default)
        String registerJson = """
            { "email": "author@test.com", "password": "secret12" }
        """;

        String token = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwt = objectMapper.readValue(token, AuthResponse.class).getToken();

        // Try to create author -> should be FORBIDDEN (MEMBER role)
        String authorJson = """
            { "name": "J. R. R. Tolkien" }
        """;

        mockMvc.perform(post("/api/v1/authors")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateAndReadAuthor() throws Exception {
        // Login as admin
        String loginJson = """
        { "email": "admin@test.com", "password": "secret12" }
    """;

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = objectMapper.readValue(loginResponse, AuthResponse.class).getToken();

        // Create author
        String authorJson = """
        { "name": "George Orwell" }
    """;

        mockMvc.perform(post("/api/v1/authors")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("George Orwell"));

        // List authors
        mockMvc.perform(get("/api/v1/authors")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("George Orwell"));
    }

}
