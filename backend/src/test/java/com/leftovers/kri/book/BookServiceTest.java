package com.leftovers.kri.book;

import com.leftovers.kri.book.dto.BookResponse;
import com.leftovers.kri.book.dto.CreateBookRequest;
import com.leftovers.kri.book.dto.UpdateBookRequest;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock BookRepository bookRepository;
    @Mock BookMapper bookMapper;
    @InjectMocks BookService bookService;

    @Test
    void getAll_returnsMappedList() {
        Book book = book(1L, "Clean Code", "Robert C. Martin", 2008);
        BookResponse response = response(1L, "Clean Code", "Robert C. Martin", 2008);

        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookMapper.toResponse(book)).thenReturn(response);

        List<BookResponse> result = bookService.getAll();

        assertThat(result).containsExactly(response);
    }

    @Test
    void getAll_emptyRepository_returnsEmptyList() {
        when(bookRepository.findAll()).thenReturn(List.of());

        assertThat(bookService.getAll()).isEmpty();
    }

    @Test
    void getById_found_returnsMappedResponse() {
        Book book = book(1L, "Clean Code", "Robert C. Martin", 2008);
        BookResponse response = response(1L, "Clean Code", "Robert C. Martin", 2008);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.toResponse(book)).thenReturn(response);

        assertThat(bookService.getById(1L)).isEqualTo(response);
    }

    @Test
    void getById_notFound_throwsEntityNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsMappedResponse() {
        CreateBookRequest request = new CreateBookRequest("Clean Code", "Robert C. Martin", 2008);
        Book entity = book(null, "Clean Code", "Robert C. Martin", 2008);
        Book saved = book(1L, "Clean Code", "Robert C. Martin", 2008);
        BookResponse response = response(1L, "Clean Code", "Robert C. Martin", 2008);

        when(bookMapper.toEntity(request)).thenReturn(entity);
        when(bookRepository.save(entity)).thenReturn(saved);
        when(bookMapper.toResponse(saved)).thenReturn(response);

        assertThat(bookService.create(request)).isEqualTo(response);
        verify(bookRepository).save(entity);
    }

    @Test
    void update_found_updatesFieldsAndReturnsResponse() {
        UpdateBookRequest request = new UpdateBookRequest("Refactoring", "Martin Fowler", 2018);
        Book book = book(1L, "Clean Code", "Robert C. Martin", 2008);
        BookResponse response = response(1L, "Refactoring", "Martin Fowler", 2018);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toResponse(book)).thenReturn(response);

        assertThat(bookService.update(1L, request)).isEqualTo(response);
        assertThat(book.getTitle()).isEqualTo("Refactoring");
        assertThat(book.getAuthor()).isEqualTo("Martin Fowler");
        assertThat(book.getPublishedYear()).isEqualTo(2018);
    }

    @Test
    void update_notFound_throwsEntityNotFoundException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.update(99L, new UpdateBookRequest("X", "Y", 2000)))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_found_deletesBook() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.delete(1L);

        verify(bookRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsEntityNotFoundException() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> bookService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");

        verify(bookRepository, never()).deleteById(any());
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Book book(Long id, String title, String author, Integer year) {
        return Book.builder().id(id).title(title).author(author).publishedYear(year).build();
    }

    private BookResponse response(Long id, String title, String author, Integer year) {
        return new BookResponse(id, title, author, year);
    }
}
