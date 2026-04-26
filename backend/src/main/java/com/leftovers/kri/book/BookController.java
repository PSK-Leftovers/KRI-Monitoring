package com.leftovers.kri.book;

import com.leftovers.kri.book.dto.BookResponse;
import com.leftovers.kri.book.dto.CreateBookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getAll() {
        return bookService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@RequestBody CreateBookRequest request) {
        return bookService.create(request);
    }
}
