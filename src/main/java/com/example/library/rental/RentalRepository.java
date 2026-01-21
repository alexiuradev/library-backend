package com.example.library.rental;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.time.LocalDateTime;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    boolean existsByUserIdAndBook_TitleAndBook_Author_NameAndReturnedAtIsNull(
            Long userId, String title, String authorName
    );

    List<Rental> findByUserIdAndReturnedAtIsNull(Long userId);

    long countByUserIdAndReturnedAtIsNull(Long userId);

    List<Rental> findByUserIdAndReturnedAtIsNullAndDueAtBefore(Long userId, LocalDateTime now);

    List<Rental> findByReturnedAtIsNullAndDueAtBefore(LocalDateTime now);

}
