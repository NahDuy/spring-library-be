package com.nad.start_spring.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItemResponse {
    String id;
    String bookId;
    String bookTitle;
    String bookImage;
    double bookPrice; // If books have price? Assuming yes or 0 for now
    int quantity;
}
