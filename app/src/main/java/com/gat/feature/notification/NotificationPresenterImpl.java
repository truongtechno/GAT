package com.gat.feature.notification;

import android.util.Log;
import com.gat.common.util.MZDebug;
import com.gat.data.exception.CommonException;
import com.gat.data.exception.LoginException;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.response.impl.NotifyEntity;
import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.domain.usecase.UseCase;
import com.gat.repository.entity.User;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by mryit on 4/27/2017.
 */

public class NotificationPresenterImpl implements NotificationPresenter {

    private static final int PER_PAGE = 10;

    private final UseCaseFactory useCaseFactory;
    private final SchedulerFactory schedulerFactory;

    private int mCurrentPage;
    private int mTotalResult;

    private UseCase<DataResultListResponse<NotifyEntity>> useCaseNotifications;
    private final Subject<DataResultListResponse<NotifyEntity>> subjectLoadNotificationsSuccess;

    private final Subject<String> subjectOnError;
    private final Subject<String> subjectOnUnAuthorization;


    //check login
    private CompositeDisposable checkLoginDisposable;
    private final Subject<String> checkLoginResultSubject;
    private final Subject<String> checkLoginInputSubject;
    private final Subject<String> checkLoginError;
    private final UseCase<User> loadLocalUser;

    private boolean isLogin;

    public NotificationPresenterImpl(UseCaseFactory useCaseFactory, SchedulerFactory schedulerFactory) {
        this.useCaseFactory = useCaseFactory;
        this.schedulerFactory = schedulerFactory;
        subjectLoadNotificationsSuccess = PublishSubject.create();
        subjectOnError = PublishSubject.create();
        subjectOnUnAuthorization = PublishSubject.create();


        this.checkLoginError = PublishSubject.create();
        checkLoginResultSubject = PublishSubject.create();
        checkLoginInputSubject = BehaviorSubject.create();

        loadLocalUser = useCaseFactory.getUser();

        isLogin = false;


        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    MZDebug.w("local login: " + user.isValid());
                    if (user.isValid()) {
                        isLogin = true;
                    } else {
                        isLogin = false;
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: suggestBooks : get local login data___________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }


    @Override
    public void onCreate() {
        mCurrentPage = 1;
        mTotalResult = 0;

        checkLoginDisposable = new CompositeDisposable(checkLoginInputSubject.
                observeOn(schedulerFactory.main()).subscribe(this::startCheckLogin));
    }

    @Override
    public void onDestroy() {
        checkLoginDisposable.dispose();
    }

    private void startCheckLogin(String input){
        if(isLogin) {
            checkLoginResultSubject.onNext("");
        }else{
            checkLoginError.onNext("");
        }
    }

    @Override
    public void loadUserNotification(boolean isRefresh) {
        MZDebug.w("_______________________________________________ loadUserNotification ");

        if (isRefresh)
            mCurrentPage = 1;

        UseCase<User> loadLocalUser = useCaseFactory.getUser();
        loadLocalUser.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(user -> {
                    if (user != null && user.isValid()) {
                        loadNotify();
                    }
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: suggestBooks : get local login data___________________ E \n\r"
                            + Log.getStackTraceString(throwable));
                })
                .execute();
    }

    private void loadNotify () {

        useCaseNotifications = useCaseFactory.getUserNotification(mCurrentPage, PER_PAGE);
        useCaseNotifications.executeOn(schedulerFactory.io())
                .returnOn(schedulerFactory.main())
                .onNext(data -> {
                    if (null == data) {
                        return;
                    }
                    MZDebug.w("______________________ loadUserNotification SUCCESS, size: " +
                            data.getResultInfo().size());
                    mCurrentPage ++;
                    mTotalResult = data.getNotifyTotal();
                    subjectLoadNotificationsSuccess.onNext(data);
                })
                .onError(throwable -> {
                    MZDebug.e("ERROR: loadUserNotification _________________________________ E \n\r"
                            + Log.getStackTraceString(throwable));

                    if (throwable instanceof LoginException) {
                        subjectOnUnAuthorization.onNext( ((LoginException)throwable).responseData().message() );
                    } else if (throwable instanceof CommonException){
                        subjectOnError.onNext( ((CommonException)throwable).getMessage() );
                    }

                })
                .execute();
    }

    @Override
    public Observable<DataResultListResponse<NotifyEntity>> onLoadUserNotificationSuccess() {
        return subjectLoadNotificationsSuccess.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onError() {
        return subjectOnError.subscribeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> onUnAuthorization() {
        return subjectOnUnAuthorization.subscribeOn(schedulerFactory.main());
    }

    @Override
    public void checkLogin() {
        checkLoginInputSubject.onNext("");
    }

    @Override
    public Observable<String> checkLoginSucess() {
        return checkLoginResultSubject.observeOn(schedulerFactory.main());
    }

    @Override
    public Observable<String> checkLoginFailed() {
        return checkLoginError.observeOn(schedulerFactory.main());
    }
}
