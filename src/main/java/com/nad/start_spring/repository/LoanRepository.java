package com.nad.start_spring.repository;

import com.nad.start_spring.dto.response.UserResponse;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, String> {
    Optional<Loan> findByUser_IdAndStatus(String userId, String status);
    List<Loan> findByUser_IdAndStatusIn(String id, List<String> statuses);
    Optional<Loan> findByUserAndStatus(User user, String status);
}