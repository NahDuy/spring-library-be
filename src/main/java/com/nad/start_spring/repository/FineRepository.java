package com.nad.start_spring.repository;

import com.nad.start_spring.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, String> {
    List<Fine> findByLoanDetail_Loan_User_Id(String userId);
    boolean existsByLoanDetail_LoanDetailId(String loanDetailId);
    Optional<Fine> findByLoanDetail_LoanDetailId(String loanDetailId);


}
