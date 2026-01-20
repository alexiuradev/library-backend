package com.example.library.rental;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByBookCopyIdAndReturnedAtIsNull(Long bookCopyId);

    List<Rental> findByUserIdAndReturnedAtIsNull(Long userId);

    long countByUserIdAndReturnedAtIsNull(Long userId);

    List<Rental> findByUserIdAndReturnedAtIsNullAndDueAtBefore(Long userId, LocalDateTime now);

    List<Rental> findByReturnedAtIsNullAndDueAtBefore(LocalDateTime now);

}
