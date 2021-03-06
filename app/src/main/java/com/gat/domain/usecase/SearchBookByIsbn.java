package com.gat.domain.usecase;

import com.gat.data.response.ServerResponse;
import com.gat.repository.BookRepository;
import com.gat.repository.entity.Book;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 3/16/17.
 */

public class SearchBookByIsbn extends UseCase<Integer> {
    private final BookRepository repository;
    private final String isbn;

    public SearchBookByIsbn(BookRepository bookRepository, String isbn) {
        this.repository = bookRepository;
        this.isbn = isbn;
    }
    @Override
    protected Observable<Integer> createObservable() {
        return repository.searchBookByIsbn(isbn);
    }
}
