package com.example.library.rental;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OverdueScheduler {

    private final RentalRepository rentalRepository;

    @Scheduled(fixedDelay = 300000) // every 5 minutes
    @Transactional
    public void markOverdueRentals() {
        List<Rental> overdue = rentalRepository
                .findByStatusAndDueAtBefore(RentalStatus.ACTIVE, LocalDateTime.now());

        overdue.forEach(Rental::markOverdue);
    }
}
