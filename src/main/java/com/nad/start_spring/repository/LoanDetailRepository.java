package com.nad.start_spring.repository;

import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.dto.response.LoanResponse;
import com.nad.start_spring.entity.Book;
import com.nad.start_spring.entity.Loan;
import com.nad.start_spring.entity.LoanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanDetailRepository extends JpaRepository<LoanDetail, String> {
    List<LoanDetail> findByLoan_LoanId(String loanId);
    Optional<LoanDetail> findByLoanAndBook(Loan loan, Book book);
    List<LoanDetail> findByLoan(Loan loan);
    List<LoanDetail> findByLoan_User_Id(String userId);
    List<LoanDetail> findByLoan_User_IdAndStatus(String userId, String status);
    
    @Query("SELECT ld FROM LoanDetail ld WHERE ld.dueDate = :dueDate AND ld.returnDate IS NULL")
    List<LoanDetail> findByDueDate(@Param("dueDate") LocalDate dueDate);
    
    List<LoanDetail> findByDueDateBeforeAndReturnDateIsNull(LocalDate date);
}