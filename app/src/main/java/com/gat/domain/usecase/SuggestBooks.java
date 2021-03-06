package com.gat.domain.usecase;

import com.gat.data.response.BookResponse;
import com.gat.repository.BookRepository;

import java.util.List;
import io.reactivex.Observable;

/**
 * Created by mozaa on 30/03/2017.
 */

public class SuggestBooks extends UseCase<List<BookResponse>>  {

    private final BookRepository repository;

    public SuggestBooks(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<List<BookResponse>> createObservable() {
        return this.repository.suggestBooksWithoutLogin();
    }
}
