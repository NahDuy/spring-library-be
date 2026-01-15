package com.nad.start_spring.repository;

import com.nad.start_spring.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, String> {
    List<Favorite> findByUser_Id(String userId);

    boolean existsByUser_IdAndBook_BookID(String userId, String bookId);

    void deleteByUser_IdAndBook_BookID(String userId, String bookId);
}
