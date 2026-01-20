package com.example.library.book;

import com.example.library.author.Author;
import com.example.library.author.AuthorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository,
                       BookCopyRepository bookCopyRepository,
                       AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.authorService = authorService;
    }

    public Book createBook(String title, String isbn, Integer year, Long authorId) {
        if (bookRepository.existsByIsbn(isbn)) {
            throw new IllegalArgumentException("Book with this ISBN already exists");
        }

        Author author = authorService.getAuthor(authorId);

        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublicationYear(year);
        book.setAuthor(author);

        return bookRepository.save(book);
    }

    public void addCopies(Long bookId, int count) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        for (int i = 0; i < count; i++) {
            BookCopy copy = new BookCopy();
            copy.setBook(book);
            copy.setStatus(BookCopy.CopyStatus.AVAILABLE);
            bookCopyRepository.save(copy);
        }
    }
}
