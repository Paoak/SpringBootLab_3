package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.validation.BookValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service("jpaBookService")
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookDto existBook = getBookById(bookDto.getId());

        if (bookDto.getTitle() != null) {
            existBook.setTitle(bookDto.getTitle());
        }

        if (bookDto.getAuthor() != null) {
            existBook.setAuthor(bookDto.getAuthor());
        }

        if (bookDto.getPageCount() != 0) {
            existBook.setPageCount(bookDto.getPageCount());
        }

        if (BookValidator.isValidBook(existBook)) {
            existBook = bookMapper.bookToBookDto(bookRepository.save(bookMapper.bookDtoToBook(existBook)));
            log.info("Update book: {}", existBook);
        }

        return existBook;
    }


    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book with ID=" + id + " not found!"));
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public List<BookDto> getBooksByUserId(Long userId) {
        log.info("Get user's books with userId={}", userId);
        return bookRepository.findAllByPersonId(userId)
                .stream()
                .map(bookMapper::bookToBookDto)
                .toList();
    }

    @Override
    public void deleteBookById(Long id) {
        try {
            bookRepository.deleteById(id);
            log.info("Deleted book with ID={}", id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Book with ID=" + id + " not found!");
        }
    }

    @Transactional
    @Override
    public void deleteBooksByUserId(Long id) {
        bookRepository.deleteByPersonId(id);
    }
}
