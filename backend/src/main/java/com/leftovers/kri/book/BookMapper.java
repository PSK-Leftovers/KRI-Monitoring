package com.leftovers.kri.book;

import com.leftovers.kri.book.dto.BookResponse;
import com.leftovers.kri.book.dto.CreateBookRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookResponse toResponse(Book book);

    Book toEntity(CreateBookRequest request);
}
