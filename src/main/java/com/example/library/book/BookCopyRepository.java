package com.example.library.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    @Query("select c from BookCopy c where c.book.id = :bookId and c.status = 'AVAILABLE'")
    List<BookCopy> findAvailableCopies(Long bookId);

    long countByBookIdAndStatus(Long bookId, BookCopy.CopyStatus status);
}
