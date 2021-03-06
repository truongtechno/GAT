package com.gat.feature.book_detail.self_update_reading;

import android.util.Log;

import com.gat.common.util.MZDebug;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.BookReadingInfo;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/20/2017.
 */

public class SelfUpdateReadingPresenterImpl implements SelfUpdateReadingPresenter {


    private UseCaseFactory useCaseFactory;
    private SchedulerFactory schedulerFactory;

    private UseCase<ServerResponse> useCaseUpdateReadingStatus;
    private final Subject<String> subjectUpdateReadingStatus;

    private final Subject<String> subjectUnAuthorization;
    private final Subject<String> subjectError;


    public SelfUpdateReadingPresenterImpl(UseCaseFactory useCaseFactory,
                                          SchedulerFactory schedulerFactory) {

        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectUpdateReadingStatus = PublishSubject.create();
        subjectError = PublishSubject.create();
        subjectUnAuthorization = PublishSubject.create();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void updateReadingStatus(int editionId, int state, Integer readingId, int bookid) {
        MZDebug.i("__________ updateReadingStatus, reading status = " + state);

        useCaseUpdateReadingStatus = useCaseFactory.selfUpdateReadingStatus(editionId, state, readingId, bookid);
        useCaseUpdateReadingStatus.executeOn(schedulerFactory.io()).
                returnOn(schedulerFactory.main()).
                onNext(serverResponse -> {
                    subjectUpdateReadingStatus.onNext(serverResponse.message());
                }).
                onError(throwable -> {
                    MZDebug.e("ERROR: getBookEvaluationByUser ______________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof LoginException) {
                        LoginException exception = (LoginException) throwable;
                        subjectUnAuthorization.onNext(exception.responseData().message());
                    } else {
                        subjectError.onNext( ((CommonException)throwable).getMessage() );
                    }

                }).execute();

    }

    @Override
    public Observable<String> onUpdateReadingStatusSuccess() {
        return subjectUpdateReadingStatus.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUnAuthorization() {
        return subjectUnAuthorization.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return subjectError.subscribeOn(schedulerFactory.main());
    }
}
