package com.gat.domain.usecase;

import com.gat.data.response.BookResponse;
import com.gat.repository.BookRepository;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mryit on 3/26/2017.
 */

public class SuggestMostBorrowing extends UseCase<List<BookResponse>> {

    private final BookRepository repository;

    public SuggestMostBorrowing(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<List<BookResponse>> createObservable() {
        return this.repository.suggestMostBorrowing();
    }

}
