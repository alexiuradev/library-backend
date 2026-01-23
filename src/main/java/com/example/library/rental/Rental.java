package com.example.library.rental;

import com.example.library.book.BookCopy;
import com.example.library.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "rentals")
@Getter
@Setter
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


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RentalStatus status;


    protected Rental() {
    }

    public Rental(User user, BookCopy bookCopy, LocalDateTime dueAt) {
        this.user = user;
        this.bookCopy = bookCopy;
        this.rentedAt = LocalDateTime.now();
        this.dueAt = dueAt;
        this.status = RentalStatus.ACTIVE;
    }

    public void markReturned() {
        this.returnedAt = LocalDateTime.now();
        this.status = RentalStatus.RETURNED;
    }

    public boolean isActive() {
        return this.status == RentalStatus.ACTIVE;
    }

    public void markOverdue() {
        this.status = RentalStatus.OVERDUE;
    }


}
