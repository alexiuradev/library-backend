package com.example.library.book.dto;

import jakarta.validation.constraints.Min;

public class AddCopiesRequest {

    @Min(1)
    private int count;

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
