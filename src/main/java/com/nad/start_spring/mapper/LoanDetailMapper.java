package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.LoanDetailRequest;
import com.nad.start_spring.dto.response.LoanDetailResponse;
import com.nad.start_spring.entity.LoanDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanDetailMapper {
    @Mapping(source = "loan.loanId", target = "loanId")
    @Mapping(source = "book.bookID", target = "bookId")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "dueDate", target = "dueDate")
    @Mapping(source = "returnDate", target = "returnDate")
    LoanDetailResponse toResponse(LoanDetail loanDetail);
    LoanDetail toLoanDetail(LoanDetailRequest loanDetailRequest);
}
