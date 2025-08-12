package com.nad.start_spring.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String bookID;
    String title;
    String author;
    String description;
    int totalCopies;
    int availableCopies;
    @ElementCollection
    List<String> imageUrls = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "categoryId")
    Category category;

    @OneToMany(mappedBy = "book")
    List<LoanDetail> loanDetails;

    @OneToMany(mappedBy = "book")
    List<Reservation> reservations;
}
