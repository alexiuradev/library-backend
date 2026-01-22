package com.example.library.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    // Deterministic: get a single available copy for a book
    Optional<BookCopy> findFirstByBook_IdAndStatusOrderByIdAsc(Long bookId, BookCopy.CopyStatus status);

    // Correct property path: book.id
    long countByBook_IdAndStatus(Long bookId, BookCopy.CopyStatus status);

    // If you still want the list:
    @Query("select c from BookCopy c where c.book.id = :bookId and c.status = :status order by c.id asc")
    List<BookCopy> findByBookIdAndStatus(Long bookId, BookCopy.CopyStatus status);
}
