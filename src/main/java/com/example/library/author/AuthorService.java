package com.example.library.author;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author createAuthor(String name) {
        if (authorRepository.existsByName(name)) {
            throw new IllegalArgumentException("Author with this name already exists");
        }

        Author author = new Author();
        author.setName(name);

        return authorRepository.save(author);
    }

    @Transactional(readOnly = true)
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Author getAuthor(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));
    }
}
