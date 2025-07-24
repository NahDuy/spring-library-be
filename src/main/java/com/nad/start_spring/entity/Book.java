package com.nad.start_spring.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Book {
    @Id
    private String bookID;

    private String title;
    private String author; // Lưu tên tác giả
    private String publisher;
    private String language;
    private int totalCopies;
    private int availableCopies;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @OneToMany(mappedBy = "book")
    private List<LoanDetail> loanDetails;

    @OneToMany(mappedBy = "book")
    private List<Reservation> reservations;
}
