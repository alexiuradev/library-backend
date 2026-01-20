package com.example.library.book.dto;

public class BookResponse {

    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String authorName;
    private long availableCopies;

    public BookResponse(Long id, String title, String isbn,
                        Integer publicationYear, String authorName,
                        long availableCopies) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.authorName = authorName;
        this.availableCopies = availableCopies;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getIsbn() { return isbn; }
    public Integer getPublicationYear() { return publicationYear; }
    public String getAuthorName() { return authorName; }
    public long getAvailableCopies() { return availableCopies; }
}
