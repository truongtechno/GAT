package com.gat.repository;

import com.gat.data.response.ServerResponse;
import com.gat.repository.entity.Book;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Rey on 2/13/2017.
 */

public interface BookRepository {

    Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage);
    Observable<ServerResponse<Book>> searchBookByIsbn(String isbn);

}
