package com.nad.start_spring.controller;

import com.nad.start_spring.dto.request.BookRequest;
import com.nad.start_spring.dto.response.ApiResponse;
import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.service.BookService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {
    BookService bookService;

    @GetMapping
    ApiResponse<List<BookResponse>> getAllBooks() {
        return ApiResponse.<List<BookResponse>>builder().status(bookService.getAllBooks()).build();
    }

    @GetMapping("/category/{categoryId}")
    ApiResponse<List<BookResponse>> getBooksCategory(@PathVariable("categoryId") String categoryId) {
        return ApiResponse.<List<BookResponse>>builder().status(bookService.getBooksByCategory(categoryId)).build();
    }
    @GetMapping("/related/{categoryId}")
    public ApiResponse<List<BookResponse>> getRelatedBooks(@PathVariable("categoryId") String categoryId) {
        List<BookResponse> books = bookService.getBooksByCategory(categoryId);

        return ApiResponse.<List<BookResponse>>builder()
                .status(books.stream().limit(5).collect(Collectors.toList()))
                .build();
    }

    @GetMapping("/search")
    public ApiResponse<List<BookResponse>> searchBooks(@RequestParam(value = "query", required = false) String query) {
        List<BookResponse> result = bookService.searchBooks(query);
        return ApiResponse.<List<BookResponse>>builder().status(result).build();
    }


    @GetMapping("/{id}")
    ApiResponse<BookResponse> getBook(@PathVariable("id") String id) {
        return ApiResponse.<BookResponse>builder().status(bookService.getBook(id)).build();
    }
    @GetMapping("entity/{id}")
    ApiResponse<Book> getEtityBook(@PathVariable("id") String id) {
        return ApiResponse.<Book>builder().status(bookService.getBookById(id)).build();
    }


    @PostMapping
    ApiResponse<BookResponse> createBook(@RequestBody BookRequest request) {
        return ApiResponse.<BookResponse>builder().status(bookService.createBook(request)).build();
    }

    @PutMapping("/{id}")
    ApiResponse<BookResponse> updateBook(@PathVariable String id, @RequestBody BookRequest request) {
        return ApiResponse.<BookResponse>builder().status(bookService.updateBook(id, request)).build();
    }

    @DeleteMapping("/{bookId}")
    ApiResponse<String> deleteBook(@PathVariable("bookId") String bookId) {
        bookService.deleteBook(bookId);
        return ApiResponse.<String>builder()
                .status("Delete complete")
                .build();
    }
}
