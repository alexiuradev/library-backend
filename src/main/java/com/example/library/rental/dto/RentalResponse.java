package com.example.library.rental.dto;

import com.example.library.rental.Rental;

import java.time.LocalDateTime;

public class RentalResponse {

    private Long rentalId;
    private Long bookId;
    private String bookTitle;
    private LocalDateTime rentedAt;
    private LocalDateTime dueAt;

    public static RentalResponse from(Rental rental) {
        RentalResponse dto = new RentalResponse();
        dto.rentalId = rental.getId();
        dto.bookId = rental.getBookCopy().getBook().getId();
        dto.bookTitle = rental.getBookCopy().getBook().getTitle();
        dto.rentedAt = rental.getRentedAt();
        dto.dueAt = rental.getDueAt();
        return dto;
    }

    public Long getRentalId() { return rentalId; }
    public Long getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public LocalDateTime getRentedAt() { return rentedAt; }
    public LocalDateTime getDueAt() { return dueAt; }
}
