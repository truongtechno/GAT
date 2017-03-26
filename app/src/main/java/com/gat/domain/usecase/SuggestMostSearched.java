package com.gat.domain.usecase;

import com.gat.repository.BookRepository;
import com.gat.repository.entity.Book;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by mryit on 3/26/2017.
 */

public class SuggestMostSearched extends UseCase<List<Book>> {

    private final BookRepository repository;

    public SuggestMostSearched(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    protected Observable<List<Book>> createObservable() {
        return this.repository.suggestMostSearched();
    }

}
