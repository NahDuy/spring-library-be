package com.nad.start_spring.service;

import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Favorite;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.mapper.BookMapper;
import com.nad.start_spring.repository.BookRepository;
import com.nad.start_spring.repository.FavoriteRepository;
import com.nad.start_spring.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FavoriteService {
    FavoriteRepository favoriteRepository;
    UserRepository userRepository;
    BookRepository bookRepository;
    BookMapper bookMapper;

    @Transactional
    public String toggleFavorite(String bookId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (favoriteRepository.existsByUser_IdAndBook_BookID(user.getId(), bookId)) {
            favoriteRepository.deleteByUser_IdAndBook_BookID(user.getId(), bookId);
            return "Removed from favorites";
        } else {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .book(book)
                    .createdAt(LocalDate.now())
                    .build();
            favoriteRepository.save(favorite);
            return "Added to favorites";
        }
    }

    public List<BookResponse> getMyFavorites() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return favoriteRepository.findByUser_Id(user.getId()).stream()
                .map(favorite -> bookMapper.toBookResponse(favorite.getBook()))
                .toList();
    }

    public boolean isFavorite(String bookId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || username.equals("anonymousUser"))
            return false;

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return favoriteRepository.existsByUser_IdAndBook_BookID(user.getId(), bookId);
    }
}
