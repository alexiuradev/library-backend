package com.example.library.rental;

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
class RentalControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String login(String email, String password) throws Exception {
        String json = """
            { "email": "%s", "password": "%s" }
        """.formatted(email, password);

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(response, AuthResponse.class).getToken();
    }

    @Test
    void memberCanRentAndReturnBook() throws Exception {
        String adminToken = login("admin@test.com", "secret12");
        String memberToken = login("author@test.com", "secret12");

        // Create Author
        String authorResponse = mockMvc.perform(post("/api/v1/authors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{ "name": "Isaac Asimov" }"""))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long authorId = objectMapper.readTree(authorResponse).get("id").asLong();

        // Create Book
        String bookResponse = mockMvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Foundation",
                              "isbn": "9780553293357",
                              "publicationYear": 1951,
                              "authorId": %d
                            }
                        """.formatted(authorId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long bookId = objectMapper.readTree(bookResponse).get("id").asLong();

        // Add 1 copy
        mockMvc.perform(post("/api/v1/books/{id}/copies", bookId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""{ "count": 1 }"""))
                .andExpect(status().isNoContent());

        // Rent
        String rentalResponse = mockMvc.perform(post("/api/v1/rentals/books/{id}", bookId)
                        .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long rentalId = objectMapper.readTree(rentalResponse).get("rentalId").asLong();

        // Verify in my rentals
        mockMvc.perform(get("/api/v1/rentals/me")
                        .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Foundation"));

        // Return
        mockMvc.perform(post("/api/v1/rentals/{id}/return", rentalId)
                        .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isNoContent());

        // Verify no active rentals
        mockMvc.perform(get("/api/v1/rentals/me")
                        .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
