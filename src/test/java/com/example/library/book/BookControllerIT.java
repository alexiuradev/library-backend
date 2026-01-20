package com.example.library.book;

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
class BookControllerIT {

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
    void adminCanCreateBookAndAddStock_memberCanView() throws Exception {
        String adminToken = login("admin@test.com", "secret12");

        // 1. Create Author
        String authorJson = """
            { "name": "Frank Herbert" }
        """;

        String authorResponse = mockMvc.perform(post("/api/v1/authors")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authorJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long authorId = objectMapper.readTree(authorResponse).get("id").asLong();

        // 2. Create Book
        String bookJson = """
            {
              "title": "Dune",
              "isbn": "9780441013593",
              "publicationYear": 1965,
              "authorId": %d
            }
        """.formatted(authorId);

        String bookResponse = mockMvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long bookId = objectMapper.readTree(bookResponse).get("id").asLong();

        // 3. Add 3 copies
        String copiesJson = """
            { "count": 3 }
        """;

        mockMvc.perform(post("/api/v1/books/{id}/copies", bookId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(copiesJson))
                .andExpect(status().isNoContent());

        // 4. Member views books
        String memberToken = login("author@test.com", "secret12");

        mockMvc.perform(get("/api/v1/books")
                        .header("Authorization", "Bearer " + memberToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Dune"))
                .andExpect(jsonPath("$[0].availableCopies").value(3));
    }

    @Test
    void memberCannotCreateBook() throws Exception {
        String memberToken = login("author@test.com", "secret12");

        String bookJson = """
            {
              "title": "Unauthorized Book",
              "isbn": "1111111111",
              "authorId": 1
            }
        """;

        mockMvc.perform(post("/api/v1/books")
                        .header("Authorization", "Bearer " + memberToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isForbidden());
    }
}
