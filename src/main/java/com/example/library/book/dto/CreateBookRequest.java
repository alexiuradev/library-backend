package com.example.library.book.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBookRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String isbn;

    private Integer publicationYear;

    @NotNull
    private Long authorId;

    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public Integer getPublicationYear() { return publicationYear; }
    public Long getAuthorId() { return authorId; }

    public void setTitle(String title) { this.title = title; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
}
