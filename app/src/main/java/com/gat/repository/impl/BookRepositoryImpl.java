package com.gat.repository.impl;

import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.data.response.impl.BookInfo;
import com.gat.data.response.impl.BookInstanceInfo;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.data.response.impl.EvaluationItemResponse;
import com.gat.repository.BookRepository;
import com.gat.repository.datasource.BookDataSource;
import com.gat.repository.entity.Book;

import java.util.List;

import dagger.Lazy;
import io.reactivex.Observable;

/**
 * Created by Rey on 2/14/2017.
 */

public class BookRepositoryImpl implements BookRepository {

    private final Lazy<BookDataSource> networkDataSourceLazy;
    private final Lazy<BookDataSource> localDataSourceLazy;

    public BookRepositoryImpl(Lazy<BookDataSource> networkDataSourceLazy, Lazy<BookDataSource> localDataSourceLazy){
        this.networkDataSourceLazy = networkDataSourceLazy;
        this.localDataSourceLazy = localDataSourceLazy;
    }

    @Override
    public Observable<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage) {
        return Observable.defer(() -> networkDataSourceLazy.get().searchBookByKeyword(keyword, page, sizeOfPage));
    }

    @Override
    public Observable<Book> searchBookByIsbn(String isbn) {
        return Observable.defer(() -> networkDataSourceLazy.get().searchBookByIsbn(isbn));
    }

    @Override
    public Observable<List<BookResponse>> suggestMostBorrowing() {
        return Observable.defer(()-> networkDataSourceLazy.get().suggestMostBorrowing());
    }

    @Override
    public Observable<List<BookResponse>> suggestBooksWithoutLogin() {
        return Observable.defer(() -> networkDataSourceLazy.get().suggestBooksWithoutLogin());
    }

    @Override
    public Observable<List<BookResponse>> suggestBooksAfterLogin() {
        return Observable.defer(()-> networkDataSourceLazy.get().suggestBooksAfterLogin());
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> searchBookByTitle(String title, long userId, int page, int sizeOfPage) {
        return Observable.defer(()-> networkDataSourceLazy.get().searchBookByTitle(title, userId, page, sizeOfPage));
    }

    @Override
    public Observable<DataResultListResponse<BookResponse>> searchBookByAuthor(String author, long userId, int page, int sizeOfPage) {
        return Observable.defer(()->networkDataSourceLazy.get().searchBookByAuthor(author, userId, page, sizeOfPage));
    }

    @Override
    public Observable<List<String>> getBooksSearchedKeyword() {
        return Observable.defer(()->networkDataSourceLazy.get().getBooksSearchedKeyword());
    }

    @Override
    public Observable<List<String>> getAuthorsSearchedKeyword() {
        return Observable.defer(()->networkDataSourceLazy.get().getAuthorsSearchedKeyword());
    }

    @Override
    public Observable<BookInfo> getBookInfo(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getBookInfo(editionId));
    }

    @Override
    public Observable<List<EvaluationItemResponse>> getBookEditionEvaluation(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getBookEditionEvaluation(editionId));
    }

    @Override
    public Observable<BookReadingInfo> getReadingStatus(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getReadingStatus(editionId));
    }

    @Override
    public Observable<EvaluationItemResponse> getBookEvaluationByUser(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getBookEvaluationByUser(editionId));
    }

    @Override
    public Observable<List<UserResponse>> getEditionSharingUser(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getEditionSharingUser(editionId));
    }

    @Override
    public Observable<ServerResponse> postComment(int editionId, int value, String review, boolean spoiler) {
        return Observable.defer( ()->networkDataSourceLazy.get().postComment(editionId, value, review, spoiler));
    }

    @Override
    public Observable<BookInstanceInfo> getSelfInstanceInfo(int editionId) {
        return Observable.defer( ()->networkDataSourceLazy.get().getSelfInstanceInfo(editionId));
    }

    @Override
    public Observable<ServerResponse> selfAddInstance(int editionId, int sharingStatus, String numberOfBook) {
        return Observable.defer( ()->networkDataSourceLazy.get().selfAddInstance(editionId, sharingStatus, numberOfBook));
    }

}
