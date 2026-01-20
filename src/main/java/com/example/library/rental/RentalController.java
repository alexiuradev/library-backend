package com.example.library.rental;

import com.example.library.rental.dto.RentalResponse;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    public RentalController(RentalService rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse rentBook(@PathVariable Long bookId,
                                   @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Rental rental = rentalService.rentBook(user.getId(), bookId);
        return RentalResponse.from(rental);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @PostMapping("/{rentalId}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long rentalId) {
        rentalService.returnBook(rentalId);
    }

    @PreAuthorize("hasRole('MEMBER')")
    @GetMapping("/me")
    public List<RentalResponse> myActiveRentals(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return rentalService.getActiveRentalsForUser(user.getId())
                .stream()
                .map(RentalResponse::from)
                .toList();
    }
}
