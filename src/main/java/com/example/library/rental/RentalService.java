package com.example.library.rental;

import com.example.library.book.Book;
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

        long active = rentalRepository.countByUser_IdAndReturnedAtIsNull(userId);
        if (active >= rentalProperties.getMaxActive()) {
            throw new IllegalStateException("Maximum active rentals reached");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Pick one available copy for THIS book
        BookCopy copy = bookCopyRepository
                .findFirstByBook_IdAndStatusOrderByIdAsc(bookId, BookCopy.CopyStatus.AVAILABLE)
                .orElseThrow(() -> new IllegalStateException("No available copies"));

        // The book comes from the selected copy (correct)
        Book book = copy.getBook();

        // Enforce: same title+author only once per active rental
        if (rentalRepository.existsByUser_IdAndBookCopy_Book_TitleAndBookCopy_Book_Author_NameAndReturnedAtIsNull(
                userId,
                book.getTitle(),
                book.getAuthor().getName()
        )) {
            throw new IllegalStateException("You already rented this book (same title & author)");
        }

        // Mark copy rented
        copy.setStatus(BookCopy.CopyStatus.RENTED);

        LocalDateTime dueAt = LocalDateTime.now().plusDays(DEFAULT_RENT_DAYS);
        Rental rental = new Rental(user, copy, dueAt);

        // No need to save copy explicitly; JPA will flush changes in the same transaction,
        // but it's OK if you want to keep it explicit.
        // bookCopyRepository.save(copy);

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
        return rentalRepository.findByUser_IdAndReturnedAtIsNull(userId);
    }

    @Transactional(readOnly = true)
    public List<Rental> getOverdueRentalsForUser(Long userId) {
        return rentalRepository.findByUser_IdAndReturnedAtIsNullAndDueAtBefore(
                userId, LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public List<Rental> getOverdueForUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return rentalRepository.findByUser_IdAndStatus(user.getId(), RentalStatus.OVERDUE);
    }


}
