package com.example.library.book;

import com.example.library.book.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;
    private final BookRepository bookRepository;
    private final BookCopyRepository copyRepository;

    public BookController(BookService bookService,
                          BookRepository bookRepository,
                          BookCopyRepository copyRepository) {
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.copyRepository = copyRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody CreateBookRequest request) {
        Book book = bookService.createBook(
                request.getTitle(),
                request.getIsbn(),
                request.getPublicationYear(),
                request.getAuthorId()
        );

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getAuthor().getName(),
                0
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping("/{id}/copies")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addCopies(@PathVariable Long id,
                          @Valid @RequestBody AddCopiesRequest request) {
        bookService.addCopies(id, request.getCount());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<BookResponse> listBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookResponse(
                        book.getId(),
                        book.getTitle(),
                        book.getIsbn(),
                        book.getPublicationYear(),
                        book.getAuthor().getName(),
                        copyRepository.countByBookIdAndStatus(
                                book.getId(),
                                BookCopy.CopyStatus.AVAILABLE
                        )
                ))
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public BookResponse getBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        long available = copyRepository.countByBookIdAndStatus(
                book.getId(),
                BookCopy.CopyStatus.AVAILABLE
        );

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getPublicationYear(),
                book.getAuthor().getName(),
                available
        );
    }

}
