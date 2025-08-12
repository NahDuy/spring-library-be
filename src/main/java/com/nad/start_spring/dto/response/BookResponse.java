package com.nad.start_spring.dto.response;

import jakarta.persistence.ElementCollection;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookResponse {
     String bookID;
     String title;
     String author;
     String description;
     int totalCopies;
     int availableCopies;
     @ElementCollection
     List<String> imageUrls = new ArrayList<>();
     CategoryResponse category;
     int loanCount;
     int reservationCount;

}
