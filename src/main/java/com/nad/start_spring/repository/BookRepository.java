package com.nad.start_spring.repository;

import com.nad.start_spring.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findByCategory_CategoryId(String categoryId);

    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    @Query("SELECT b FROM Book b LEFT JOIN b.loanDetails ld GROUP BY b ORDER BY COUNT(ld) DESC")
    List<Book> findMostBorrowedBooks(Pageable pageable);

    List<Book> findTop5ByCategory_CategoryIdAndBookIDNot(String categoryId, String bookID);
}
