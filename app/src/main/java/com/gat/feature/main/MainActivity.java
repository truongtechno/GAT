package com.gat.feature.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.customview.BadgeTabLayout;
import com.gat.common.customview.BadgeView;
import com.gat.common.event.NetWorkEvent;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.DataLocal;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Views;
import com.gat.common.view.NonSwipeableViewPager;
import com.gat.common.util.NotificationConfig;
import com.gat.data.firebase.NotificationUtils;
import com.gat.data.firebase.entity.Notification;
import com.gat.data.firebase.entity.NotificationParcelable;
import com.gat.data.share.SharedData;
import com.gat.domain.usecase.SignOut;
import com.gat.feature.notification.NotificationFragment;
import com.gat.common.util.Constance;
import com.gat.feature.personal.PersonalFragment;
import com.gat.feature.scanbarcode.ScanFragment;
import com.gat.feature.setting.SettingFragment;
import com.gat.feature.setting.main.MainSettingFragment;
import com.gat.feature.suggestion.SuggestionFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import de.greenrobot.event.EventBus;

/**
 * Created by mryit on 3/26/2017.
 */

public class MainActivity extends ScreenActivity<MainScreen, MainPresenter> implements IMainDelegate{

    @Retention(RetentionPolicy.SOURCE)
    public @interface TAB_POS {
        int TAB_HOME            = 0;
        int TAB_PERSONAL        = 1;
        int TAB_SCAN            = 2;
        int TAB_NOTIFICATION    = 3;
        int TAB_SETTING         = 4;
    }

    @BindView(R.id.viewPager)
    NonSwipeableViewPager mViewPager;

    @BindView(R.id.tabLayout)
    BadgeTabLayout mTabLayout;

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                NotificationParcelable parcelable = intent.getExtras().getParcelable("data");
                if (parcelable != null) {
                    Notification notification = parcelable.getNotification();
                    if (notification.pushType() == NotificationConfig.PushType.PRIVATE_MESSAGE) {
                        if (notification.senderId() != SharedData.getInstance().getMessagingUserId()) {
                            // Make notification
                            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                            Intent pushNotification = new Intent(getApplicationContext(), MainActivity.class);
                            pushNotification.putExtra("data", parcelable);
                            notificationUtils.showNotificationMessage(notification.title(),
                                    notification.message(),
                                    new Date().getTime(),
                                    intent
                            );
                        }
                    }
                }
            }
        }
    };

    public static MainActivity instance;
    private PersonalFragment personalFragment;
    private  ScanFragment scanFragment;
    private NotificationFragment notificationFragment;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected MainScreen getDefaultScreen() {
        return MainScreen.instance();
    }

    @Override
    protected Class<MainPresenter> getPresenterClass() {
        return MainPresenter.class;
    }

    @Override
    protected Object getPresenterKey() {
        return MainScreen.instance();
    }

    private int previousTab = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientUtils.context = getApplicationContext();
        DataLocal.init(getApplicationContext());
        instance = this;

        // setup view pager
        setupViewPager(mViewPager);
        // setup tab layout
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabLayoutIcons(mTabLayout);

        // set up icon high light
        mTabLayout.getTabAt(TAB_POS.TAB_HOME).setIcon(R.drawable.home_ic_selected);
        previousTab = mTabLayout.getSelectedTabPosition();
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                try {
                    scanFragment.cleanView();
                    selectTab(tab, true);// For refresh layout
                    mTabLayout.setScrollPosition(tab.getPosition(), 0, true);
                    if(tab.getPosition() == 1) {
                        personalFragment.checkLogin();
                    }
                    if (tab.getPosition() == 3) {
                        notificationFragment.checkLogin();
                    }
                }catch (Exception e){}
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            NotificationParcelable parcelable = intent.getExtras().getParcelable("data");
            if (parcelable != null)
                CommonCheck.processNotification(parcelable.getNotification(), this);
        }
        if (Notification.isValid(getScreen().notificationParcelable().getNotification())) {
            CommonCheck.processNotification(getScreen().notificationParcelable().getNotification(), this);
        }

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.gat",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("NameNotFoundException", Log.getStackTraceString(e));
        } catch (NoSuchAlgorithmException e) {
            Log.e("NameNotFoundException", Log.getStackTraceString(e));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mIntentReceiver,
                new IntentFilter(NotificationConfig.NOTIFICATION_SERVICE));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

        Views.hideKeyboard(this);
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mIntentReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constance.RESULT_UPDATEUSER){
            personalFragment.requestPersonalInfo();
        }

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if(fragment instanceof MainSettingFragment) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                    break;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        int currentTab = mTabLayout.getSelectedTabPosition();
        if (currentTab > 0) {
            currentTab --;
            setTabDesire(currentTab);
        } else {
            // finish() will call onDestroy().. this code used to back to home screen.
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
    }

    @Override
    public void goTo631PageRequest () {
        // go to page personal
        mTabLayout.setScrollPosition(TAB_POS.TAB_PERSONAL,0f,true);
        mViewPager.setCurrentItem(TAB_POS.TAB_PERSONAL);

        // go to page request in personal fragment
        personalFragment.setTabDesire(2);
    }

    @Override
    public void haveToPullNotifyPage(int pullCount) {
        MZDebug.w("haveToPullNotifyPage : pullCount = " + pullCount);
        tabNotification.setNoticeCount(pullCount);
    }


    public void setTabDesire(int position) {
        mTabLayout.setScrollPosition(position,0f,true);
        mViewPager.setCurrentItem(position);
    }


    private void setupViewPager(ViewPager viewPager) {
        personalFragment = new PersonalFragment();
        scanFragment = new ScanFragment();
        personalFragment.setMainActivity(this);
        notificationFragment = new NotificationFragment(this);
        notificationFragment.setMainActivity(this);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SuggestionFragment(), getString(R.string.tab_home));
        adapter.addFragment(personalFragment, getString(R.string.tab_personal));
        adapter.addFragment(scanFragment, getString(R.string.tab_scanbarcode));
        adapter.addFragment(notificationFragment, getString(R.string.tab_notification));
        adapter.addFragment(new SettingFragment(), getString(R.string.tab_setting));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
    }

    private BadgeTabLayout tabNotification;
    private void setupTabLayoutIcons (BadgeTabLayout tabLayout) {
        tabLayout.getTabAt(TAB_POS.TAB_HOME).setIcon(R.drawable.home_ic);
        tabLayout.getTabAt(TAB_POS.TAB_PERSONAL).setIcon(R.drawable.personal_ic);
        tabLayout.getTabAt(TAB_POS.TAB_SCAN).setIcon(R.drawable.scan_ic);
        tabNotification = tabLayout.playNotification(TAB_POS.TAB_NOTIFICATION, 0);
        tabLayout.getTabAt(TAB_POS.TAB_SETTING).setIcon(R.drawable.setting_ic);
    }

    private void selectTab(BadgeTabLayout.Tab tab, boolean select) {
        switch (tab.getPosition()) {
            case TAB_POS.TAB_HOME:
                tab.setIcon(select ? R.drawable.home_ic_selected : R.drawable.home_ic);
                break;
            case TAB_POS.TAB_PERSONAL:
                tab.setIcon(select ? R.drawable.personal_ic_selected : R.drawable.personal_ic);
                break;
            case TAB_POS.TAB_SCAN:
                tab.setIcon(select ? R.drawable.scan_ic_selected : R.drawable.scan_ic);
                break;
            case TAB_POS.TAB_NOTIFICATION:
                //tab.setIcon(select ? R.drawable.notic_ic_selected : R.drawable.notic_ic);

                tabNotification.setTabIcon(select ? R.drawable.notic_ic_selected : R.drawable.notic_ic);
                tabNotification.setTitleColor(select ? Color.parseColor("#FFFFFF") : Color.parseColor("#919191"));


                break;
            case TAB_POS.TAB_SETTING:
                tab.setIcon(select ? R.drawable.setting_ic_selected : R.drawable.setting_ic);
                break;
            default:
                throw new UnsupportedOperationException();
        }
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


}
