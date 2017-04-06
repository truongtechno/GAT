package com.gat.repository.datasource;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.BookMostBorrowing;
import com.gat.data.response.impl.BookSuggest;
import com.gat.repository.entity.Book;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;

/**
 * Created by Rey on 2/14/2017.
 */

public interface BookDataSource {

    Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage);

    Observable<Book> searchBookByIsbn(String isbn);

    Observable<List<BookMostBorrowing>> suggestMostBorrowing();

    Observable<List<BookSuggest>> suggestBooksWithoutLogin();

    Observable<List<BookSuggest>> suggestBooksAfterLogin();
}
