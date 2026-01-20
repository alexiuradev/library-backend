package com.example.library.rental;

import com.example.library.auth.CurrentUser;
import com.example.library.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    // MEMBER rents a book
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse rentBook(@PathVariable Long bookId,
                                   @CurrentUser User user) {
        Rental rental = rentalService.rentBook(user.getId(), bookId);
        return RentalResponse.from(rental);
    }

    // MEMBER returns a rented copy
    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/{rentalId}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long rentalId) {
        rentalService.returnBook(rentalId);
    }

    // MEMBER views own active rentals
    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/me")
    public List<RentalResponse> myActiveRentals(@CurrentUser User user) {
        return rentalService.getActiveRentalsForUser(user.getId())
                .stream()
                .map(RentalResponse::from)
                .toList();
    }
}
