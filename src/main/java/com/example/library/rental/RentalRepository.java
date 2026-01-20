package com.example.library.rental;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    Optional<Rental> findByBookCopyIdAndReturnedAtIsNull(Long bookCopyId);

    List<Rental> findByUserIdAndReturnedAtIsNull(Long userId);
}
