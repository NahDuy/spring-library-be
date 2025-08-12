package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.response.FineResponse;
import com.nad.start_spring.entity.Fine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface FineMapper {
    @Mapping(source = "loan.loanId", target = "loanId")
    @Mapping(source = "loanDetail.loanDetailId", target = "loanDetailId")
    @Mapping(source = "loanDetail.book.title", target = "bookTitle")
    FineResponse toResponse(Fine fine);
}
