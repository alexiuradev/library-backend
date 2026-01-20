package com.example.library.rental;

import com.example.library.book.BookCopy;
import com.example.library.book.BookCopyRepository;
import com.example.library.book.BookCopy.CopyStatus;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RentalService {

    private static final int DEFAULT_RENT_DAYS = 14;

    private final RentalRepository rentalRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;

    private final RentalProperties rentalProperties;


    public RentalService(RentalRepository rentalRepository,
                         BookCopyRepository bookCopyRepository,
                         UserRepository userRepository, RentalProperties rentalProperties) {
        this.rentalRepository = rentalRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.userRepository = userRepository;
        this.rentalProperties = rentalProperties;
    }

    public Rental rentBook(Long userId, Long bookId) {

        long active = rentalRepository.countByUserIdAndReturnedAtIsNull(userId);
        if (active >= rentalProperties.getMaxActive()) {
            throw new IllegalStateException("Maximum active rentals reached");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<BookCopy> availableCopies =
                bookCopyRepository.findAvailableCopies(bookId);

        if (availableCopies.isEmpty()) {
            throw new IllegalStateException("No available copies for this book");
        }

        BookCopy copy = availableCopies.get(0);

        copy.setStatus(CopyStatus.RENTED);
        bookCopyRepository.save(copy);

        LocalDateTime dueAt = LocalDateTime.now().plusDays(DEFAULT_RENT_DAYS);

        Rental rental = new Rental(user, copy, dueAt);
        return rentalRepository.save(rental);
    }

    public void returnBook(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        if (!rental.isActive()) {
            throw new IllegalStateException("Rental already returned");
        }

        rental.markReturned();
        rentalRepository.save(rental);

        BookCopy copy = rental.getBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);
    }

    @Transactional(readOnly = true)
    public List<Rental> getActiveRentalsForUser(Long userId) {
        return rentalRepository.findByUserIdAndReturnedAtIsNull(userId);
    }
}
