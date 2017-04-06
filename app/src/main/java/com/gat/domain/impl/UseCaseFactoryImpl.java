package com.gat.domain.impl;

import android.support.annotation.Nullable;

import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.BookMostBorrowing;
import com.gat.data.response.impl.BookSuggest;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.*;
import com.gat.repository.BookRepository;
import com.gat.repository.UserRepository;
import com.gat.repository.entity.Book;
import com.gat.repository.entity.LoginData;
import com.gat.repository.entity.User;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.concurrent.Callable;

import dagger.Lazy;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;

/**
 * Created by Rey on 2/13/2017.
 */

public class UseCaseFactoryImpl implements UseCaseFactory {

    private final Lazy<BookRepository> bookRepositoryLazy;
    private final Lazy<UserRepository> userRepositoryLazy;

    public UseCaseFactoryImpl(Lazy<BookRepository> bookRepositoryLazy,
                              Lazy<UserRepository> userRepositoryLazy){
        this.bookRepositoryLazy = bookRepositoryLazy;
        this.userRepositoryLazy = userRepositoryLazy;
    }

    @Override
    public UseCase<List<Book>> searchBookByKeyword(String keyword, int page, int sizeOfPage) {
        return new SearchBookByKeyword(bookRepositoryLazy.get(), keyword, page, sizeOfPage);
    }

    @Override
    public UseCase<Book> getBookByIsbn(String isbn) {
        return new SearchBookByIsbn(bookRepositoryLazy.get(), isbn);
    }

    @Override
    public UseCase<User> getUser() {
        return new GetUser(userRepositoryLazy.get());
    }

    @Override
    public UseCase<User> login(LoginData data) {
        return new Login(userRepositoryLazy.get(), data);
    }

    @Override
    public UseCase<LoginData> getLoginData() {
        return new GetLoginData(userRepositoryLazy.get());
    }

    @Override
    public UseCase<ServerResponse> sendRequestResetPassword(String email) {
        return new SendRequestResetPassword(userRepositoryLazy.get(), email);
    }

    @Override
    public UseCase<ServerResponse> verifyResetToken(String code) {
        return new VerifyResetToken(userRepositoryLazy.get(), code);
    }

    @Override
    public UseCase<ServerResponse> resetPassword(String password) {
        return new ResetPassword(userRepositoryLazy.get(), password);
    }

    @Override
    public UseCase<User> register(LoginData data) {
        return new Register(userRepositoryLazy.get(), data);
    }

    @Override
    public UseCase<ServerResponse> updateLocation(String address, LatLng location) {
        return new UpdateLocation(userRepositoryLazy.get(), address, location);
    }

    @Override
    public UseCase<ServerResponse> updateCategories(List<Integer> categories) {
        return new UpdateCategory(userRepositoryLazy.get(), categories);
    }



    @Override
    public <T, R> UseCase<R> transform(UseCase<T> useCase, ObservableTransformer<T, R> transformer, @Nullable Scheduler transformScheduler) {
        TransformUseCase transformUseCase = new TransformUseCase<>(useCase, transformer);
        if(transformScheduler != null)
            transformUseCase.transformOn(transformScheduler);
        return transformUseCase;
    }

    @Override
    public <T> UseCase<T> doWork(Callable<T> callable) {
        return new WorkUseCase<>(callable);
    }

    @Override
    public UseCase<List<BookMostBorrowing>> suggestMostBorrowing() {
        return new SuggestMostBorrowing(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<BookSuggest>> suggestBooks() {
        return new SuggestBooks(bookRepositoryLazy.get());
    }

    @Override
    public UseCase<List<UserNearByDistance>> peopleNearByUser(LatLng userLocation, LatLng neLocation, LatLng wsLocation) {
        return new PeopleNearByUser(userRepositoryLazy.get(), userLocation, neLocation, wsLocation);
    }

}
