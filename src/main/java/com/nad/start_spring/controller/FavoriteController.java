package com.nad.start_spring.controller;

import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.service.FavoriteService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteController {
    FavoriteService favoriteService;

    @PostMapping("/{bookId}")
    public ApiResponse<String> toggleFavorite(@PathVariable String bookId) {
        return ApiResponse.<String>builder()
                .status(favoriteService.toggleFavorite(bookId))
                .build();
    }

    @GetMapping
    public ApiResponse<List<BookResponse>> getMyFavorites() {
        return ApiResponse.<List<BookResponse>>builder()
                .status(favoriteService.getMyFavorites())
                .build();
    }

    @GetMapping("/check/{bookId}")
    public ApiResponse<Boolean> checkFavorite(@PathVariable String bookId) {
        return ApiResponse.<Boolean>builder()
                .status(favoriteService.isFavorite(bookId))
                .build();
    }
}
