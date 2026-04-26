package com.leftovers.kri.book;

import com.leftovers.kri.book.dto.BookResponse;
import com.leftovers.kri.book.dto.CreateBookRequest;
import com.leftovers.kri.book.dto.UpdateBookRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public List<BookResponse> getAll() {
        log.debug("Fetching all books");
        List<BookResponse> books = bookRepository.findAll().stream()
                .map(bookMapper::toResponse)
                .toList();
        log.debug("Found {} book(s)", books.size());
        return books;
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        log.debug("Fetching book id={}", id);
        return bookRepository.findById(id)
                .map(bookMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Book not found id={}", id);
                    return new EntityNotFoundException("Book not found with id: " + id);
                });
    }

    public BookResponse create(CreateBookRequest request) {
        log.info("Creating book title='{}' author='{}'", request.title(), request.author());
        Book saved = bookRepository.save(bookMapper.toEntity(request));
        log.info("Created book id={}", saved.getId());
        return bookMapper.toResponse(saved);
    }

    public BookResponse update(Long id, UpdateBookRequest request) {
        log.info("Updating book id={}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found for update id={}", id);
                    return new EntityNotFoundException("Book not found with id: " + id);
                });
        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPublishedYear(request.publishedYear());
        log.info("Updated book id={}", id);
        return bookMapper.toResponse(bookRepository.save(book));
    }

    public void delete(Long id) {
        log.info("Deleting book id={}", id);
        if (!bookRepository.existsById(id)) {
            log.warn("Book not found for deletion id={}", id);
            throw new EntityNotFoundException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Deleted book id={}", id);
    }
}
