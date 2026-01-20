package com.example.library.rental;

import com.example.library.book.BookCopy;
import com.example.library.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

    @Column(nullable = false)
    private LocalDateTime rentedAt;

    @Column(nullable = false)
    private LocalDateTime dueAt;

    private LocalDateTime returnedAt;

    protected Rental() {
    }

    public Rental(User user, BookCopy bookCopy, LocalDateTime dueAt) {
        this.user = user;
        this.bookCopy = bookCopy;
        this.rentedAt = LocalDateTime.now();
        this.dueAt = dueAt;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public LocalDateTime getRentedAt() {
        return rentedAt;
    }

    public LocalDateTime getDueAt() {
        return dueAt;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    public void markReturned() {
        this.returnedAt = LocalDateTime.now();
    }
}
