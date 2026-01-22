package com.example.library.rental;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    boolean existsByUser_IdAndBookCopy_Book_TitleAndBookCopy_Book_Author_NameAndReturnedAtIsNull(
            Long userId, String title, String authorName
    );

    List<Rental> findByUser_IdAndReturnedAtIsNull(Long userId);

    long countByUser_IdAndReturnedAtIsNull(Long userId);

    List<Rental> findByUser_IdAndReturnedAtIsNullAndDueAtBefore(Long userId, LocalDateTime now);

    List<Rental> findByReturnedAtIsNullAndDueAtBefore(LocalDateTime now);
}
