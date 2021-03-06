package com.gat.feature.suggestion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esotericsoftware.kryo.serializers.VersionFieldSerializer;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.app.fragment.ScreenFragment;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.common.util.TrackGPS;
import com.gat.data.response.BookResponse;
import com.gat.data.response.DataResultListResponse;
import com.gat.data.share.SharedData;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.book_detail.BookDetailScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.message.GroupMessageActivity;
import com.gat.feature.message.presenter.GroupMessageScreen;
import com.gat.feature.personaluser.PersonalUserActivity;
import com.gat.feature.personaluser.PersonalUserScreen;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceActivity;
import com.gat.feature.suggestion.nearby_user.ShareNearByUserDistanceScreen;
import com.gat.feature.suggestion.search.SuggestSearchActivity;
import com.gat.feature.suggestion.search.SuggestSearchScreen;
import com.gat.repository.entity.UserNearByDistance;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import pl.droidsonroids.gif.GifTextView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by mryit on 3/26/2017.
 */

public class SuggestionFragment extends ScreenFragment<SuggestionScreen, SuggestionPresenter>
        implements EasyPermissions.PermissionCallbacks, BookSuggestAdapter.IBookSuggestItemClickListener{
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1986;

    @BindView(R.id.image_button_search)
    ImageButton textViewTitle;

    @BindView(R.id.recycler_view_most_borrowing)
    RecyclerView mRecyclerViewMostBorrowing;

    @BindView(R.id.recycler_view_suggest_books)
    RecyclerView mRecyclerViewSuggestBooks;

    @BindView(R.id.ll_user_near_parent)
    LinearLayout llUseNearParent;

    @BindView(R.id.ll_user_near_suggest)
    LinearLayout llUserNearSuggest;

    @BindView(R.id.unread_count)
    TextView unReadGroupMessageCnt;

    @BindView(R.id.gif_text_view_suggest)
    GifTextView gifTextViewSuggest;

    @BindView(R.id.gif_text_view_most_borrow)
    GifTextView gifTextViewMostBorrow;

    private CompositeDisposable disposables;
    private BookSuggestAdapter mMostBorrowingAdapter;
    private BookSuggestAdapter mBookSuggestAdapter;
    private List<BookResponse> mListBookMostBorrowing;
    private List<BookResponse> mListBookSuggest;

    private TrackGPS gps;
    private List<UserNearByDistance> mListUserDistance;
    private LatLng currentLatLng;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_suggestion;
    }

    @Override
    protected SuggestionScreen getDefaultScreen() {
        return SuggestionScreen.instance();
    }

    @Override
    protected Class<SuggestionPresenter> getPresenterClass() {
        return SuggestionPresenter.class;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListBookMostBorrowing = new ArrayList<>();
        mListBookSuggest = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // setup recycler view linear
        mRecyclerViewMostBorrowing.setHasFixedSize(true);
        mRecyclerViewMostBorrowing.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewMostBorrowing.setNestedScrollingEnabled(false);

        mRecyclerViewSuggestBooks.setHasFixedSize(true);
        mRecyclerViewSuggestBooks.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewSuggestBooks.setNestedScrollingEnabled(false);

        disposables = new CompositeDisposable(
                getPresenter().onTopBorrowingSuccess().subscribe(this::onTopBorrowingSuccess),
                getPresenter().onBookSuggestSuccess().subscribe(this::onSuggestBooksSuccess),
                getPresenter().onPeopleNearByUserSuccess().subscribe(this::onPeopleNearByUserByDistanceSuccess),
                getPresenter().onError().subscribe(this::onError),
                SharedData.getInstance().getBadgeSubject().observeOn(AndroidSchedulers.mainThread()).distinctUntilChanged().subscribe(badge -> {
                    if (getUserVisibleHint()) {
                        displayBadge();
                    }
                })
        );

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (mListBookMostBorrowing.isEmpty()) {
            getPresenter().suggestMostBorrowing();
        }
        if (mListBookSuggest.isEmpty()) {
            getPresenter().suggestBooks();
        }

        if (llUserNearSuggest.getChildCount() < 2) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                processLocationToUpdateUserShareNearByDistance();
            } else {

                if (gps == null) {
                    gps = new TrackGPS(getActivity());
                }

                if (gps.isLocationEnabled(mContext) && gps.isGPSAvailable()) {
                    // Have permission, remove button request permission
                    llUserNearSuggest.removeAllViews();

                    // request list user near
                    processUserNearByDistance();
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        disposables.dispose();

        if (gps != null)
            gps.stopUsingGPS();

        super.onDestroy();
    }

    @OnClick(R.id.rl_group_message)
    void onButtonGoMessageTap () {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null)
            ScreenActivity.start(getContext(), GroupMessageActivity.class, GroupMessageScreen.instance());
    }

    @OnClick(R.id.button_go_setting)
    void onButtonGoSettingToEnablePermissionTap() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @OnClick(R.id.image_button_search)
    void onSearchButtonTap() {
        MainActivity.start(mContext, SuggestSearchActivity.class, SuggestSearchScreen.instance());
    }

    @OnClick(R.id.button_more_sharing_near)
    void onMoreSharingNearTap() {

        MainActivity.start(mContext, ShareNearByUserDistanceActivity.class, ShareNearByUserDistanceScreen.instance());

//        getActivity().finish();
//
//        Intent intent = new Intent(mContext, ShareNearByUserDistanceActivity.class);
//        Bundle bundle = new Bundle();
//
//        intent.putExtra(EXTRA_SCREEN, new ParcelableScreen(ShareNearByUserDistanceScreen.instance()))
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        bundle.putParcelableArrayList(ShareNearByUserDistanceActivity.PASS_LIST_USER_DISTANCE,
//                (ArrayList<? extends Parcelable>) mListUserDistance);
//        bundle.putDouble(ShareNearByUserDistanceActivity.PASS_USER_LOCATION_LATITUDE, currentLatLng.latitude);
//        bundle.putDouble(ShareNearByUserDistanceActivity.PASS_USER_LOCATION_LONGITUDE, currentLatLng.longitude);
//        intent.putExtras(bundle);
//        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(PERMISSION_ACCESS_COARSE_LOCATION)
    private void processLocationToUpdateUserShareNearByDistance() {
        MZDebug.i("_______________________________ processLocationToUpdateUserShareNearByDistance");
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            requestUserNear ();
        } else {
            MZDebug.w("onPermissionsDenied -_- ");
            // Request one permission
            EasyPermissions.requestPermissions(this, mContext.getResources().getString(R.string.msg_allow_location),
                    PERMISSION_ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        MZDebug.w("onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    @Override
    public void onItemBookClickListener(View view, BookResponse book) {
        MainActivity.start(getActivity(), BookDetailActivity.class, BookDetailScreen.instance( (int) book.getEditionId()));
    }

    private void displayBadge() {
        int badge = SharedData.getInstance().getBadge();
        if (badge > 0) {
            unReadGroupMessageCnt.setVisibility(View.VISIBLE);
            unReadGroupMessageCnt.setText(badge + "");
        } else {
            unReadGroupMessageCnt.setVisibility(View.INVISIBLE);
        }
    }

    private void requestUserNear () {
        if (gps == null) {
            gps = new TrackGPS(getActivity());
        }
        if (gps.isGPSAvailable()) {
            // Have permission, remove button request permission
            llUserNearSuggest.removeAllViews();

            // request list user near
            processUserNearByDistance();
        }
    }

    private void processUserNearByDistance() {
        double longitude = gps.getLongitude();
        double latitude = gps.getLatitude();
        MZDebug.i("longitude: " + longitude + ", lat: " + latitude);
        currentLatLng = new LatLng(latitude, longitude);

        LatLng neLocation = new LatLng(latitude +1, longitude +1);
        LatLng wsLocation = new LatLng(latitude -1, longitude -1);

        getPresenter().getPeopleNearByUser(currentLatLng, neLocation, wsLocation);
    }


    void onPeopleNearByUserByDistanceSuccess(DataResultListResponse<UserNearByDistance> data) {
        MZDebug.i("_________________________________________ onPeopleNearByUserByDistance Success");
        if (llUserNearSuggest.getChildCount() > 0) {
            return;
        }

        mListUserDistance = data.getResultInfo(); // use this to pass along to ShareNearByUserDistanceActivity

        if (null == mListUserDistance || mListUserDistance.isEmpty()) {
            TextView textView = new TextView(mContext);
            textView.setText(mContext.getResources().getString(R.string.msg_no_user_near));
            textView.setWidth(llUserNearSuggest.getWidth());
            textView.setHeight(llUserNearSuggest.getHeight());

            llUserNearSuggest.addView(textView);
            return; // no user found, so return from there
        }

        View viewItem;
        UserNearByDistance userItem;
        int size = mListUserDistance.size() < 10 ? mListUserDistance.size() : 10; // show 10 users

        for (int i = 0; i < size; i++) {
            userItem = mListUserDistance.get(i);
            MZDebug.w(userItem.toString());
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewItem = inflater.inflate(R.layout.item_user_near_suggest, llUserNearSuggest, false);

            // set width for user items
            llUseNearParent.getWidth();
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) viewItem.getLayoutParams();
            params.width=  (int) Math.floor(llUseNearParent.getWidth()/5);
            viewItem.setLayoutParams(params);

            // init view
            ImageView imageViewAvatar = (ImageView) viewItem.findViewById(R.id.iv_people_near_suggest_avatar);
            TextView textViewName = (TextView) viewItem.findViewById(R.id.tv_people_near_suggest_name);

            // set data
            if ( ! TextUtils.isEmpty(userItem.getImageId())) {
                ClientUtils.setImage(mContext, imageViewAvatar, R.drawable.default_user_icon,
                        ClientUtils.getUrlImage(userItem.getImageId(), ClientUtils.SIZE_SMALL));
            }

            textViewName.setText(userItem.getName());
            imageViewAvatar.setTag(userItem); // pass object -> tag

            imageViewAvatar.setOnClickListener(view -> {

                UserNearByDistance item = (UserNearByDistance) view.getTag();
                MainActivity.start(getActivity().getApplicationContext(), PersonalUserActivity.class,
                        PersonalUserScreen.instance( (int) item.getUserId()));
            });

            // add child view
            llUserNearSuggest.addView(viewItem);
        }
    }

    void onTopBorrowingSuccess(List<BookResponse> list) {

        gifTextViewMostBorrow.setVisibility(View.GONE);

        mListBookMostBorrowing.addAll(list);
        if (null == mMostBorrowingAdapter) {
            mMostBorrowingAdapter = new BookSuggestAdapter(mContext, mListBookMostBorrowing, this);
            mRecyclerViewMostBorrowing.setAdapter(mMostBorrowingAdapter);
            return;
        }
        mMostBorrowingAdapter.notifyDataSetChanged();
    }

    void onSuggestBooksSuccess(List<BookResponse> list) {

        gifTextViewSuggest.setVisibility(View.GONE);

        mListBookSuggest.addAll(list);
        if (null == mBookSuggestAdapter) {
            mBookSuggestAdapter = new BookSuggestAdapter(getActivity(), mListBookSuggest, this);
            mRecyclerViewSuggestBooks.setAdapter(mBookSuggestAdapter);
            return;
        }
        mBookSuggestAdapter.notifyDataSetChanged();
    }


    void onError(String message) {
        MZDebug.e(message);
    }

}
