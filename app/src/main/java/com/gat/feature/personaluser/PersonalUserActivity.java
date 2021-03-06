package com.gat.feature.personaluser;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.UserResponse;
import com.gat.feature.login.LoginActivity;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.message.MessageActivity;
import com.gat.feature.message.presenter.MessageScreen;
import com.gat.feature.personal.entity.BookReadingInput;
import com.gat.feature.personaluser.entity.BookSharingUserInput;
import com.gat.feature.personaluser.entity.BorrowRequestInput;
import com.gat.feature.personaluser.fragment.FragmentBookUserReading;
import com.gat.feature.personaluser.fragment.FragmentBookUserSharing;
import com.gat.repository.entity.Data;
import com.gat.repository.entity.User;
import com.gat.repository.entity.book.BookReadingEntity;
import com.gat.repository.entity.book.BookSharingEntity;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;
import pl.droidsonroids.gif.GifTextView;

/**
 * Created by root on 20/04/2017.
 */

public class PersonalUserActivity extends ScreenActivity<PersonalUserScreen, PersonalUserPresenter> {

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    NonSwipeableViewPager viewPager;

    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgChat;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.layoutMenutop)
    RelativeLayout layoutMenutop;

    @BindView(R.id.layoutBack)
    RelativeLayout layoutBack;

    @BindView(R.id.loadingInfo)
    GifTextView loadingInfo;
    private Context context;

    //init fragment
    private FragmentBookUserSharing fragmentBookUserSharing;


    private FragmentBookUserReading fragmentBookUserReading;
    private TextView txtNumberSharing, txtNumberReading;
    private CompositeDisposable disposablesBookUserSharing;


    private CompositeDisposable disposablesBookUserReading;
    private CompositeDisposable disposableBorrowBook;
    private CompositeDisposable disposableUserVisitorInfo;
    private CompositeDisposable disposablesCheckLogin;
    private User currentUser;

    int userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getScreen().userId();

        context = getApplicationContext();
        disposablesBookUserSharing = new CompositeDisposable(getPresenter().getResponseBookUserSharing().subscribe(this::getBookUserSharingSuccess),
                getPresenter().onErrorBookUserSharing().subscribe(this::getBookUserSharingError));

        disposablesBookUserReading = new CompositeDisposable(getPresenter().getResponseBookUserReading().subscribe(this::getBookUserReadingSuccess),
                getPresenter().onErrorBookUserReading().subscribe(this::getBookUserSharingError));

        disposableBorrowBook = new CompositeDisposable(getPresenter().getResponseBorrowBook().subscribe(this::borrowBookSuccess),
                getPresenter().onErrorBorrowBook().subscribe(this::borrowBookError));

        disposableUserVisitorInfo = new CompositeDisposable(getPresenter().getResponseVisitorInfo().subscribe(this::getUserVisitorInfoSuccess),
                getPresenter().onErrorVisitorInfo().subscribe(this::getBookUserSharingError));
        disposablesCheckLogin = new CompositeDisposable(getPresenter().checkLoginSucess().subscribe(this::checkLoginSuccess),
                getPresenter().checkLoginFailed().subscribe(this::checkLoginFailed));

        requestUserVisitorInfo(userId);
    }

    private void initView() {
        layoutMenutop.setBackgroundColor(Color.parseColor("#8ec3df"));
        txtTitle.setText("CÁ NHÂN");
        imgChat.setImageResource(R.drawable.ic_chat_white);
        if (currentUser != null) {
            if (!Strings.isNullOrEmpty(currentUser.name())) {
                txtName.setText(currentUser.name());
            }
            if (currentUser.usuallyLocation().size() > 0) {
                if (!Strings.isNullOrEmpty(currentUser.usuallyLocation().get(0).getAddress())) {
                    txtAddress.setText(currentUser.usuallyLocation().get(0).getAddress());
                }
            }
            if (!Strings.isNullOrEmpty(currentUser.imageId())) {
                String url = ClientUtils.getUrlImage(currentUser.imageId(), Constance.IMAGE_SIZE_ORIGINAL);
                ClientUtils.setImage(this, imgAvatar, R.drawable.ic_profile, url);
            }
        }

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void requestUserVisitorInfo(int userId) {
        checkInternet();
        getPresenter().requestVisitorInfo(userId);
        loadingInfo.setVisibility(View.VISIBLE);
    }

    private void handleEvent() {
        layoutBack.setOnClickListener(v -> finish());

        //event chating
        imgChat.setOnClickListener(v -> {
            if (currentUser == null) return;
            start(getApplicationContext(), MessageActivity.class, MessageScreen.instance(currentUser.userId()));
        });
    }

    private void setupTabIcons() {
        View tabOne = LayoutInflater.from(context).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabOne = (ImageView) tabOne.findViewById(R.id.imgCircle);
        imgTabOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_loanbook));
        txtNumberSharing = (TextView) tabOne.findViewById(R.id.txtNumber);
        txtNumberSharing.setText("0");
        TextView txtTitleOne = (TextView) tabOne.findViewById(R.id.txtTitle);
        txtTitleOne.setText("Sách cho mượn");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo = LayoutInflater.from(context).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabTwo = (ImageView) tabTwo.findViewById(R.id.imgCircle);
        imgTabTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_readingbook));
        txtNumberReading = (TextView) tabTwo.findViewById(R.id.txtNumber);
        txtNumberReading.setText("0");
        TextView txtTitleTwo = (TextView) tabTwo.findViewById(R.id.txtTitle);
        txtTitleTwo.setText("Sách đang đọc");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        tabLayout.setSelectedTabIndicatorHeight(0);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (fragmentBookUserSharing == null) {
            fragmentBookUserSharing = new FragmentBookUserSharing();
            fragmentBookUserSharing.setParrentActivity(this);
            try {
                BookSharingUserInput input = new BookSharingUserInput();
                input.setOwnerId(currentUser.userId());
                fragmentBookUserSharing.setCurrentInput(input);
            } catch (Exception e) {
            }
        }
        if (fragmentBookUserReading == null) {
            fragmentBookUserReading = new FragmentBookUserReading();
            fragmentBookUserReading.setParrentActivity(this);
            try {
                BookReadingInput currentInput = new BookReadingInput(false, true, false);
                currentInput.setUserId(currentUser.userId());
                fragmentBookUserReading.setCurrentInput(currentInput);
            } catch (Exception e) {
            }
        }
        adapter.addFragment(fragmentBookUserSharing, "");
        adapter.addFragment(fragmentBookUserReading, "");
        viewPager.setAdapter(adapter);
    }

    private void checkInternet() {
        if (!ClientUtils.isOnline()) {
            ClientUtils.showViewNotInternet(MainActivity.instance, layoutMenutop);
            return;
        }
    }

    public void requestBookUserSharing(BookSharingUserInput input) {
        checkInternet();
        getPresenter().requestBookUserSharing(input);
    }

    private void getBookUserSharingSuccess(Data data) {
        if (data != null) {
            int totalSharing = data.getTotalSharing();
            txtNumberSharing.setText(totalSharing + "");
            List<BookSharingEntity> list = data.getListDataReturn(BookSharingEntity.class);
            fragmentBookUserSharing.setListBook(list);

        }
    }

    public void checkLogin() {
        getPresenter().checkLogin();
    }

    private void checkLoginSuccess(String input) {
        //do nothing
    }

    private void checkLoginFailed(String input) {
    }

    private void getBookUserSharingError(ServerResponse<ResponseData> error) {
        loadingInfo.setVisibility(View.GONE);
        ClientUtils.showDialogError(this, ClientUtils.getStringLanguage(R.string.titleError), error.message());
    }

    private void borrowBookError(String error) {
        fragmentBookUserSharing.hideLoadBook();
        ClientUtils.showDialogError(this, ClientUtils.getStringLanguage(R.string.titleError), error);
    }

    private void getUserVisitorInfoSuccess(User user) {
        loadingInfo.setVisibility(View.GONE);
        if (user != null) {
            currentUser = user;
            initView();
            handleEvent();
        }
    }

    private void borrowBookSuccess(Data data) {
        if (data != null) {
            if (!Strings.isNullOrEmpty(data.getMessage())) {
                ClientUtils.showToast(this, data.getMessage());
            }

            BookSharingEntity entity = (BookSharingEntity) data.getDataReturn(BookSharingEntity.class);
            fragmentBookUserSharing.refreshAdapterSharingBook(entity.getRecordStatus());
        }
        fragmentBookUserSharing.hideLoadBook();
    }

    private void getBookUserReadingSuccess(Data data) {
        if (data != null) {
            List<BookReadingEntity> list = data.getListDataReturn(BookReadingEntity.class);
            fragmentBookUserReading.setListBook(list);
            int totalReading = data.getReadingTotal();
            txtNumberReading.setText(totalReading + "");
        }
    }

    public void requestBorrowBook(BorrowRequestInput input) {
        checkInternet();
        input.setOwnerId(currentUser.userId());
        getPresenter().requestBorrowBook(input);
        fragmentBookUserSharing.showLoadBook();
    }


    public void requestBookUserReading(BookReadingInput input) {
        checkInternet();
        getPresenter().requestBookUserReading(input);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.layout_personaluser_activity;
    }

    @Override
    protected Class<PersonalUserPresenter> getPresenterClass() {
        return PersonalUserPresenter.class;
    }

    @Override
    protected PersonalUserScreen getDefaultScreen() {
        return PersonalUserScreen.instance(getScreen().userId());
    }

    @Override
    protected Object getPresenterKey() {
        return PersonalUserScreen.instance(getScreen().userId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposablesBookUserSharing.dispose();
        disposablesBookUserReading.dispose();
        disposableBorrowBook.dispose();
        disposableUserVisitorInfo.dispose();
    }
}
