package com.nad.start_spring.dto.request;

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
public class BookRequest {
     String title;
     String author;
     String description;
     int totalCopies;
     @ElementCollection
     List<String> imageUrls = new ArrayList<>();

     String categoryId;
}
