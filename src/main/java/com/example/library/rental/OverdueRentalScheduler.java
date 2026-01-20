package com.example.library.rental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OverdueRentalScheduler {

    private static final Logger log = LoggerFactory.getLogger(OverdueRentalScheduler.class);

    private final RentalRepository rentalRepository;

    public OverdueRentalScheduler(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    // Runs every day at 02:00
    @Scheduled(cron = "0 0 2 * * *")
    public void scanOverdueRentals() {
        List<Rental> overdue = rentalRepository
                .findByReturnedAtIsNullAndDueAtBefore(LocalDateTime.now());

        if (!overdue.isEmpty()) {
            log.warn("Found {} overdue rentals", overdue.size());
            overdue.forEach(r ->
                    log.warn("Overdue: rentalId={}, userId={}, book={}",
                            r.getId(),
                            r.getUser().getId(),
                            r.getBookCopy().getBook().getTitle())
            );
        }
    }
}
