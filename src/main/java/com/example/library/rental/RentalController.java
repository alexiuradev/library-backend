package com.example.library.rental;

import com.example.library.rental.dto.RentalResponse;
import com.example.library.user.User;
import com.example.library.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;
    public static final String EXCEPT_MESSAGE ="User not found!";

    public RentalController(RentalService rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    @PostMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse rentBook(@PathVariable Long bookId,
                                   @AuthenticationPrincipal String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(EXCEPT_MESSAGE));

        Rental rental = rentalService.rentBook(user.getId(), bookId);
        return RentalResponse.from(rental);
    }

    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    @PostMapping("/{rentalId}/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long rentalId) {
        rentalService.returnBook(rentalId);
    }

    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    @GetMapping("/me")
    public List<RentalResponse> myActiveRentals(@AuthenticationPrincipal String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException(EXCEPT_MESSAGE));

        return rentalService.getActiveRentalsForUser(user.getId())
                .stream()
                .map(RentalResponse::from)
                .toList();
    }


    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    @GetMapping("/me/overdue")
    public List<RentalResponse> myOverdue(@AuthenticationPrincipal String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return rentalService.getOverdueRentalsForUser(user.getId())
                .stream()
                .map(RentalResponse::from)
                .toList();
    }

}
