package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.LoanRequest;
import com.nad.start_spring.dto.response.LoanResponse;
import com.nad.start_spring.entity.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { LoanDetailMapper.class, UserMapper.class })
public interface LoanMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "user")
    LoanResponse toResponse(Loan loan);

    Loan toLoan(LoanRequest loanRequest);
}
