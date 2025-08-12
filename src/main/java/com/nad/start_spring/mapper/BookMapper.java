package com.nad.start_spring.mapper;

import com.nad.start_spring.dto.request.BookRequest;
import com.nad.start_spring.dto.response.BookResponse;
import com.nad.start_spring.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {
    Book toBook(BookRequest bookRequest);
    BookResponse toBookResponse(Book book);
    void updateEntity(@MappingTarget Book book, BookRequest request);
}
