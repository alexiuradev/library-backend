package com.example.library.book;

import com.example.library.author.Author;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String isbn;

    private Integer publicationYear;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookCopy> copies = new ArrayList<>();

    // getters & setters

    public Long getId() { return id; }

    public String getTitle() { return title; }

    public String getIsbn() { return isbn; }

    public Integer getPublicationYear() { return publicationYear; }

    public Author getAuthor() { return author; }

    public List<BookCopy> getCopies() { return copies; }

    public void setId(Long id) { this.id = id; }

    public void setTitle(String title) { this.title = title; }

    public void setIsbn(String isbn) { this.isbn = isbn; }

    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }

    public void setAuthor(Author author) { this.author = author; }

    public void addCopy(BookCopy copy) {
        copies.add(copy);
        copy.setBook(this);
    }
}
