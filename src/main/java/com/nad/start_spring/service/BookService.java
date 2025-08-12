package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.BookRequest;
import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Category;
import com.nad.start_spring.mapper.BookMapper;
import com.nad.start_spring.repository.BookRepository;
import com.nad.start_spring.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookService {

    BookRepository bookRepository;
    CategoryRepository categoryRepository;
    BookMapper bookMapper;
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookResponse).toList();
    }
    public List<BookResponse> getBooksByCategory(String categoryId) {
        return bookRepository.findByCategory_CategoryId(categoryId)
                .stream()
                .map(bookMapper::toBookResponse)
                .toList();
    }

    public BookResponse createBook(BookRequest request) {
        Book book = bookMapper.toBook(request);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        book.setCategory(category);
        return bookMapper.toBookResponse(bookRepository.save(book));
    }
    public Book getBookById(String bookId) {
        Optional<Book> book = bookRepository.findById(bookId);
        return book.orElse(null);
    }

    public BookResponse updateBook(String id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookMapper.updateEntity(book, request);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        book.setCategory(category);
        return bookMapper.toBookResponse(bookRepository.save(book));
    }

    public BookResponse getBook(String id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
    public List<BookResponse> searchBooks(String query) {
        List<Book> books;
        if (query == null || query.trim().isEmpty()) {
            books = bookRepository.findAll();
        } else {
            books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
        }
        return books.stream().map(bookMapper::toBookResponse).toList();
    }

    public void deleteBook(String id) {
        bookRepository.deleteById(id);
    }
}
