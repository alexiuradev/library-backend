package com.example.library.book;

import jakarta.persistence.*;

@Entity
@Table(name = "book_copies")
public class BookCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CopyStatus status;

    public enum CopyStatus {
        AVAILABLE,
        RENTED,
        LOST
    }

    // getters & setters

    public Long getId() { return id; }

    public Book getBook() { return book; }

    public CopyStatus getStatus() { return status; }

    public void setId(Long id) { this.id = id; }

    public void setBook(Book book) { this.book = book; }

    public void setStatus(CopyStatus status) { this.status = status; }
}
