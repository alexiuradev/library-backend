package com.example.library.author;

import com.example.library.author.dto.AuthorResponse;
import com.example.library.author.dto.CreateAuthorRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // Only ADMIN or LIBRARIAN can create authors
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(@Valid @RequestBody CreateAuthorRequest request) {
        Author author = authorService.createAuthor(request.getName());
        return new AuthorResponse(author.getId(), author.getName());
    }

    // Any authenticated user can view authors
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<AuthorResponse> getAllAuthors() {
        return authorService.getAllAuthors()
                .stream()
                .map(a -> new AuthorResponse(a.getId(), a.getName()))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public AuthorResponse getAuthor(@PathVariable Long id) {
        Author author = authorService.getAuthor(id);
        return new AuthorResponse(author.getId(), author.getName());
    }
}
