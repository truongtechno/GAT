package com.gat.feature.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.adapter.ViewPagerAdapter;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.feature.notification.NotificationFragment;
import com.gat.feature.personal.PersonalFragment;
import com.gat.feature.scanbarcode.ScanFragment;
import com.gat.feature.suggestion.SuggestionFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;

/**
 * Created by ducbtsn on 5/6/17.
 */

public class ResultActivity extends ScreenActivity<MainScreen, MainPresenter> implements IMainDelegate{
    @Override
    public void goTo631PageRequest() {

    }

    @Override
    public void haveToPullNotifyPage(int pullCount) {
        // no nothing
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TAB_POS {
        int TAB_HOME            = 0;
        int TAB_PERSONAL        = 1;
        int TAB_SCAN            = 2;
        int TAB_NOTIFICATION    = 3;
        int TAB_SETTING         = 4;
    }

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    private PersonalFragment personalFragment;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ClientUtils.context = getApplicationContext();

        // setup view pager
        setupViewPager(mViewPager);
        // setup tab layout
        mTabLayout.setupWithViewPager(mViewPager);
        setupTabLayoutIcons(mTabLayout);
        // set up icon high light
        mTabLayout.getTabAt(MainActivity.TAB_POS.TAB_HOME).setIcon(R.drawable.home_ic_selected);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab, true);// For refresh layout
                mTabLayout.setScrollPosition(tab.getPosition(), 0, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                selectTab(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your dialog to ask if user is going out
            // ...

            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        // your dialog to ask if user is going out
        // ...

        moveTaskToBack(true);
    }

    private void setupViewPager(ViewPager viewPager) {
        personalFragment = new PersonalFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SuggestionFragment(), "HOME PAGE");
        adapter.addFragment(personalFragment, "PERSONAL");
        adapter.addFragment(new ScanFragment(), "SCAN");
        adapter.addFragment(new NotificationFragment(this), "NOTICE");
        adapter.addFragment(new Fragment(), "SETTING");
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
    }

    private void setupTabLayoutIcons (TabLayout tabLayout) {
        tabLayout.getTabAt(MainActivity.TAB_POS.TAB_HOME).setIcon(R.drawable.home_ic);
        tabLayout.getTabAt(MainActivity.TAB_POS.TAB_PERSONAL).setIcon(R.drawable.personal_ic);
        tabLayout.getTabAt(MainActivity.TAB_POS.TAB_SCAN).setIcon(R.drawable.scan_ic);
        tabLayout.getTabAt(MainActivity.TAB_POS.TAB_NOTIFICATION).setIcon(R.drawable.notic_ic);
        tabLayout.getTabAt(MainActivity.TAB_POS.TAB_SETTING).setIcon(R.drawable.setting_ic);
    }

    private void selectTab(TabLayout.Tab tab, boolean select) {
        switch (tab.getPosition()) {
            case MainActivity.TAB_POS.TAB_HOME:
                tab.setIcon(select ? R.drawable.home_ic_selected : R.drawable.home_ic);
                break;
            case MainActivity.TAB_POS.TAB_PERSONAL:
                tab.setIcon(select ? R.drawable.personal_ic_selected : R.drawable.personal_ic);
                break;
            case MainActivity.TAB_POS.TAB_SCAN:
                tab.setIcon(select ? R.drawable.scan_ic_selected : R.drawable.scan_ic);
                break;
            case MainActivity.TAB_POS.TAB_NOTIFICATION:
                tab.setIcon(select ? R.drawable.notic_ic_selected : R.drawable.notic_ic);
                break;
            case MainActivity.TAB_POS.TAB_SETTING:
                tab.setIcon(select ? R.drawable.setting_ic_selected : R.drawable.setting_ic);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Constance.RESULT_UPDATEUSER){
            personalFragment.requestPersonalInfo();
        }
    }
}
