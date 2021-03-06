package com.gat.dependency;

import com.gat.domain.SchedulerFactory;
import com.gat.domain.UseCaseFactory;
import com.gat.feature.book_detail.BookDetailPresenter;
import com.gat.feature.book_detail.BookDetailPresenterImpl;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcasePresenter;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcasePresenterImpl;
import com.gat.feature.book_detail.comment.CommentPresenter;
import com.gat.feature.book_detail.comment.CommentPresenterImpl;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookPresenter;
import com.gat.feature.book_detail.list_user_sharing_book.ListUserSharingBookPresenterImpl;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingPresenter;
import com.gat.feature.book_detail.self_update_reading.SelfUpdateReadingPresenterImpl;
import com.gat.feature.bookdetailowner.BookDetailOwnerPresenter;
import com.gat.feature.bookdetailowner.BookDetailOwnerPresenterImpl;
import com.gat.feature.bookdetailsender.BookDetailSenderPresenter;
import com.gat.feature.bookdetailsender.BookDetailSenderPresenterImpl;
import com.gat.feature.editinfo.EditInfoPresenter;
import com.gat.feature.editinfo.EditInfoPresenterImpl;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginPresenterImpl;
import com.gat.feature.main.MainPresenter;
import com.gat.feature.main.MainPresenterImpl;
import com.gat.feature.message.presenter.GroupMessagePresenter;
import com.gat.feature.message.presenter.GroupMessagePresenterImpl;
import com.gat.feature.message.presenter.MessagePresenter;
import com.gat.feature.message.presenter.MessagePresenterImpl;
import com.gat.feature.notification.NotificationPresenter;
import com.gat.feature.notification.NotificationPresenterImpl;
import com.gat.feature.personal.PersonalPresenter;
import com.gat.feature.personal.PersonalPresenterImpl;
import com.gat.feature.personaluser.PersonalUserPresenter;
import com.gat.feature.personaluser.PersonalUserPresenterImpl;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.RegisterPresenterImpl;
import com.gat.feature.register.update.category.AddCategoryPresenter;
import com.gat.feature.register.update.category.AddCategoryPresenterImpl;
import com.gat.feature.register.update.location.AddLocationPresenter;
import com.gat.feature.register.update.location.AddLocationPresenterImpl;
import com.gat.feature.scanbarcode.ScanPresenter;
import com.gat.feature.scanbarcode.ScanPresenterImpl;
import com.gat.feature.search.SearchItemBuilder;
import com.gat.feature.search.SearchPresenter;
import com.gat.feature.search.SearchPresenterImpl;
import com.gat.feature.setting.account_social.SocialConnectedPresenter;
import com.gat.feature.setting.account_social.SocialConnectedPresenterImpl;
import com.gat.feature.setting.add_email_password.AddEmailPasswordPresenter;
import com.gat.feature.setting.add_email_password.AddEmailPasswordPresenterImpl;
import com.gat.feature.setting.change_password.ChangePasswordPresenter;
import com.gat.feature.setting.change_password.ChangePasswordPresenterImpl;
import com.gat.feature.setting.main.MainSettingPresenter;
import com.gat.feature.setting.main.MainSettingPresenterImpl;
import com.gat.feature.suggestion.SuggestionPresenter;
import com.gat.feature.suggestion.SuggestionPresenterImpl;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistancePresenter;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistancePresenterImpl;
import com.gat.feature.suggestion.search.SuggestSearchPresenter;
import com.gat.feature.suggestion.search.SuggestSearchPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Rey on 2/15/2017.
 */
@Module
public class PresenterModule {

    @Provides
    SearchItemBuilder provideSearchItemBuilder() {
        return new SearchItemBuilder();
    }

    @Provides
    SearchPresenter provideSearchPresenter(UseCaseFactory useCaseFactory,
                                           SchedulerFactory schedulerFactory,
                                           SearchItemBuilder itemBuilder) {
        return new SearchPresenterImpl(useCaseFactory, schedulerFactory, itemBuilder);
    }

    @Provides
    LoginPresenter provideLoginPresenter(UseCaseFactory useCaseFactory,
                                         SchedulerFactory schedulerFactory) {
        return new LoginPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    RegisterPresenter provideRegisterPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory) {
        return new RegisterPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddLocationPresenter provideAddLocationPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory) {
        return new AddLocationPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddCategoryPresenter provideAddCategoryPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory) {
        return new AddCategoryPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    SuggestionPresenter provideSuggestionPresenter(UseCaseFactory useCaseFactory,
                                                   SchedulerFactory schedulerFactory) {
        return new SuggestionPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    MessagePresenter provideMessagePresenter(UseCaseFactory useCaseFactory,
                                             SchedulerFactory schedulerFactory) {
        return new MessagePresenterImpl(useCaseFactory, schedulerFactory);
    }


    @Provides
    GroupMessagePresenter provideGroupMessagePresenter(UseCaseFactory useCaseFactory,
                                                  SchedulerFactory schedulerFactory){
        return new GroupMessagePresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    MainPresenter provideMainPresenter(UseCaseFactory useCaseFactory,
                                       SchedulerFactory schedulerFactory) {
        return new MainPresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    ShareNearByUserDistancePresenter provideShareNearByUserDistance(UseCaseFactory useCaseFactory,
                                                                    SchedulerFactory schedulerFactory) {
        return new ShareNearByUserDistancePresenterImpl(useCaseFactory, schedulerFactory) {
        };
    }

    @Provides
    SuggestSearchPresenter provideSuggestSearchPresenter(UseCaseFactory useCaseFactory,
                                                         SchedulerFactory schedulerFactory) {
        return new SuggestSearchPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    PersonalPresenter providePersonalPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory) {
        return new PersonalPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    EditInfoPresenter provideEditInfoPresenter(UseCaseFactory useCaseFactory,
                                               SchedulerFactory schedulerFactory) {
        return new EditInfoPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    PersonalUserPresenter providePersonalUserPresenter(UseCaseFactory useCaseFactory,
                                                       SchedulerFactory schedulerFactory) {
        return new PersonalUserPresenterImpl(useCaseFactory, schedulerFactory);
    }

    //@Provides
    //BookDetailPresenter provideBookDetailRequestPresenter(UseCaseFactory useCaseFactory,
    //                                                            SchedulerFactory schedulerFactory) {
    //    return new BookDetailPresenterImpl(useCaseFactory, schedulerFactory);
    //}


    @Provides
    BookDetailSenderPresenter getBookDetailSenderPresenter( UseCaseFactory useCaseFactory,
                                                                SchedulerFactory schedulerFactory) {
        return new BookDetailSenderPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    BookDetailPresenter provideBookDetailPresenter(UseCaseFactory useCaseFactory,
                                                   SchedulerFactory schedulerFactory) {
        return new BookDetailPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    SelfUpdateReadingPresenter provideSelfUpdateReadingPresenter(UseCaseFactory useCaseFactory,
                                                                 SchedulerFactory schedulerFactory) {
        return new SelfUpdateReadingPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    ListUserSharingBookPresenter provideListUserSharingBookPresenter(UseCaseFactory useCaseFactory,
                                                                     SchedulerFactory schedulerFactory) {
        return new ListUserSharingBookPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddToBookcasePresenter provideAddToBookcasePresenter(UseCaseFactory useCaseFactory,
                                                         SchedulerFactory schedulerFactory) {
        return new AddToBookcasePresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    CommentPresenter provideCommentPresenter(UseCaseFactory useCaseFactory,
                                             SchedulerFactory schedulerFactory) {
        return new CommentPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    ScanPresenter provideScanPresenter(UseCaseFactory useCaseFactory,
                                       SchedulerFactory schedulerFactory) {
        return new ScanPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    MainSettingPresenter provideMainSettingPresenter(UseCaseFactory useCaseFactory,
                                                     SchedulerFactory schedulerFactory) {
        return new MainSettingPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    AddEmailPasswordPresenter provideAddEmailPasswordPresenter(UseCaseFactory useCaseFactory,
                                                               SchedulerFactory schedulerFactory) {
        return new AddEmailPasswordPresenterImpl(useCaseFactory, schedulerFactory);
    }


    @Provides
    SocialConnectedPresenter provideSocialConnectedPresenter(UseCaseFactory useCaseFactory,
                                                             SchedulerFactory schedulerFactory) {
        return new SocialConnectedPresenterImpl(useCaseFactory, schedulerFactory);
    }


    @Provides
    ChangePasswordPresenter provideChangePasswordPresenter(UseCaseFactory useCaseFactory,
                                                           SchedulerFactory schedulerFactory) {
        return new ChangePasswordPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    BookDetailOwnerPresenter provideBookDetailBorrowPresenter(UseCaseFactory useCaseFactory,
                                                              SchedulerFactory schedulerFactory) {
        return new BookDetailOwnerPresenterImpl(useCaseFactory, schedulerFactory);
    }

    @Provides
    NotificationPresenter provideNotificationPresenter (UseCaseFactory useCaseFactory,
                                                        SchedulerFactory schedulerFactory) {
        return new NotificationPresenterImpl(useCaseFactory, schedulerFactory);
    }


}
