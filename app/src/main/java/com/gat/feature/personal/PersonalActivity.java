package com.gat.feature.personal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;

import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.Strings;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.feature.personal.entity.BookEntity;
import com.gat.feature.personal.entity.BookInstanceInput;
import com.gat.feature.personal.fragment.FragmentBookRequest;
import com.gat.feature.personal.fragment.FragmentBookSharing;
import com.gat.feature.personal.fragment.FragmentReadingBook;
import com.gat.repository.entity.Data;
import com.gat.feature.personal.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by truongtechno on 23/03/2017.
 */

public class PersonalActivity extends ScreenActivity<PersonalScreen,PersonalPresenter> {

//    @BindView(R.id.imgAvatar)
    CircleImageView imgAvatar;

    @BindView(R.id.txtName)
    TextView txtName;

    @BindView(R.id.txtAddress)
    TextView txtAddress;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    NonSwipeableViewPager viewPager;

    private CompositeDisposable disposablesPersonal;
    private CompositeDisposable disposablesBookInstance;

    //init fragment
    private FragmentBookSharing fragmentBookSharing;
    private FragmentReadingBook fragmentBookReading;
    private FragmentBookRequest fragmentBookRequest;
    private TextView txtNumberSharing;
    private TextView txtNumberReading;
    private TextView txtNumberRequest;

    @Override
    protected PersonalScreen getDefaultScreen() {
        return PersonalScreen.instance();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_personal_activity;
    }

    @Override
    protected Class<PersonalPresenter> getPresenterClass() {
        return PersonalPresenter.class;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ClientUtils.context = getApplicationContext();
        imgAvatar = (CircleImageView) findViewById(R.id.imgAvatar);
        disposablesPersonal = new CompositeDisposable(getPresenter().getResponsePersonal().subscribe(this::getUserInfoSuccess),
                getPresenter().onErrorPersonal().subscribe(this::getUserInfoError));

        disposablesBookInstance = new CompositeDisposable(getPresenter().getResponseBookInstance().subscribe(this::getBookInstanceSuccess),
                getPresenter().onErrorBookInstance().subscribe(this::getBookInstanceError));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        //default request book
//        BookInstanceInput input = new BookInstanceInput(true, false, false);
//        requestBookInstance(input);
        handleEvent();
    }

    private void handleEvent(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    private void setupTabIcons() {

        View tabOne =  LayoutInflater.from(this).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabOne = (ImageView) tabOne.findViewById(R.id.imgCircle);
        imgTabOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_loanbook));
        txtNumberSharing = (TextView) tabOne.findViewById(R.id.txtNumber);
        txtNumberSharing.setText("0");
        TextView txtTitleOne = (TextView) tabOne.findViewById(R.id.txtTitle);
        txtTitleOne.setText("Sách cho mượn");
        tabLayout.getTabAt(0).setCustomView(tabOne);

        View tabTwo =  LayoutInflater.from(this).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabTwo = (ImageView) tabTwo.findViewById(R.id.imgCircle);
        imgTabTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_readingbook));
        txtNumberReading = (TextView) tabTwo.findViewById(R.id.txtNumber);
        txtNumberReading.setText("0");
        TextView txtTitleTwo = (TextView) tabTwo.findViewById(R.id.txtTitle);
        txtTitleTwo.setText("Sách đang đọc");
        tabLayout.getTabAt(1).setCustomView(tabTwo);
//
        View tabThree =  LayoutInflater.from(this).inflate(R.layout.layout_tab_book, null);
        ImageView imgTabThree = (ImageView) tabThree.findViewById(R.id.imgCircle);
        imgTabThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_circle_requestbook));
        txtNumberRequest = (TextView) tabThree.findViewById(R.id.txtNumber);
        txtNumberRequest.setText("0");
        TextView txtTitleThree = (TextView) tabThree.findViewById(R.id.txtTitle);
        txtTitleThree.setText("Yêu cầu");
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if(fragmentBookSharing == null){
            fragmentBookSharing = new FragmentBookSharing();
            fragmentBookSharing.setParrentActivity(this);
        }
        if(fragmentBookReading == null) {
            fragmentBookReading = new FragmentReadingBook();
        }
        if (fragmentBookRequest == null) {
            fragmentBookRequest = new FragmentBookRequest();
        }
        adapter.addFrag(fragmentBookSharing, "");
        adapter.addFrag(fragmentBookReading, "");
        adapter.addFrag(fragmentBookRequest, "");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("On Back");
    }

    //handle data personal return
    private void getUserInfoSuccess(Data data){
        if(data != null){
                UserInfo userInfo = (UserInfo) data.getDataReturn(UserInfo.class);
            if (!Strings.isNullOrEmpty(userInfo.getName())) {
                txtName.setText(userInfo.getName());
            }
            if (!Strings.isNullOrEmpty(userInfo.getEmail())) {
                txtAddress.setText(userInfo.getEmail());
            }
            if (!Strings.isNullOrEmpty(userInfo.getImageid())) {
                String url = ClientUtils.getUrlImage(userInfo.getImageid(), Constance.IMAGE_SIZE_ORIGINAL);
                ClientUtils.setImage(imgAvatar, R.drawable.ic_profile, url);
            }
        }
    }
    private void getUserInfoError(ServerResponse<ResponseData> error) {
        Toast.makeText(this, error.message(), Toast.LENGTH_SHORT).show();
    }


    public void requestBookInstance(BookInstanceInput input) {
        getPresenter().requestBookInstance(input);
    }

    //handle get bookInstance return
    private void getBookInstanceSuccess(Data data){
        if(data != null) {
            int totalSharing = data.getTotalSharing();
            txtNumberSharing.setText(totalSharing+"");
            int totalNotSharing = data.getTotalNotSharing();
            int lostTotal = data.getLostTotal();
            List<BookEntity> listBook = data.getListDataReturn(BookEntity.class);
            fragmentBookSharing.setListBook(listBook);
        }
    }
    private void getBookInstanceError(ServerResponse<ResponseData> error){
        Toast.makeText(this, error.message(), Toast.LENGTH_SHORT).show();
    }

    private void fakeData(){
        //fake data listbook sharing
        List<BookEntity> list = new ArrayList<>();
        for (int i=0; i< 10; i++) {
            BookEntity info = new BookEntity();
            info.setTitle("Cuộc đời, sự nghiệp Steve Jobs"+i);
            info.setAuthor("Frank Luca" + i);
            info.setRateCount(4);
            info.setBorrowingUserName("Trần Duy Hưng "+i);
            list.add(info);
        }
//        fragmentBookSharing.setListBook(list);

    }
}
