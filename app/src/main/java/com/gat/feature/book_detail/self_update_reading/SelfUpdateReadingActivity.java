package com.gat.feature.book_detail.self_update_reading;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.MZDebug;
import com.gat.feature.book_detail.BookDetailActivity;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 4/17/2017.
 */

public class SelfUpdateReadingActivity
        extends ScreenActivity<SelfUpdateReadingScreen, SelfUpdateReadingPresenter>
        implements RadioGroup.OnCheckedChangeListener{

    @BindView(R.id.radio_group)
    RadioGroup radioGroup;

    @BindView(R.id.radio_want_to_read)
    RadioButton radioButtonWantToRead;

    @BindView(R.id.radio_reading_book)
    RadioButton radioButtonReadingBook;

    @BindView(R.id.radio_red_book)
    RadioButton radioButtonRedBook;

    @BindView(R.id.image_button_save)
    ImageView imageButtonSave;

    private int mReadingState;
    private int mNewReadingState = -2;

    AlertDialog errorDialog;
    AlertDialog changedValueDialog;
    AlertDialog loadingDialog;
    AlertDialog unAuthorizationDialog;
    AlertDialog removeConfirmDialog;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_self_update_reading;
    }

    @Override
    protected Class<SelfUpdateReadingPresenter> getPresenterClass() {
        return SelfUpdateReadingPresenter.class;
    }

    private CompositeDisposable disposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disposable = new CompositeDisposable(
                getPresenter().onUpdateReadingStatusSuccess().subscribe(this::onUpdateReadingStatusSuccess),
                getPresenter().onUnAuthorization().subscribe(this::onUnAuthorization),
                getPresenter().onError().subscribe(this::onError)
        );
        loadingDialog = ClientUtils.createLoadingDialog(this);
        radioGroup.setOnCheckedChangeListener(this);

        mReadingState = getScreen().readingStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setChecked (mReadingState);
        updateImageState();
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();

        if (errorDialog != null) {
            errorDialog.dismiss();
        }
        if (changedValueDialog != null) {
            changedValueDialog.dismiss();
        }
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        if (unAuthorizationDialog != null) {
            unAuthorizationDialog.dismiss();
        }
        if (removeConfirmDialog != null) {
            removeConfirmDialog.dismiss();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mNewReadingState == mReadingState) {
            super.onBackPressed();
        } else {
            ClientUtils.showChangedValueDialog(this);
        }
    }

    @OnClick(R.id.image_view_back)
    void onBack () {
        if (mNewReadingState == mReadingState) {
            finish();
        } else {
            ClientUtils.showChangedValueDialog(this);
        }
    }

    @OnClick(R.id.button_remove_from_reading_list)
    void onRemoveBookFromReadingList () {
        mNewReadingState = ReadingState.REMOVE;
        // show dialog warning
        // yes -> update reading status = -1
        // no -> do nothing

        removeConfirmDialog = ClientUtils.showAlertDialog(this, getString(R.string.remove_from_reading_list),
                getString(R.string.ask_remove_from_reading_list), getString(R.string.yes), getString(R.string.no),
                new ClientUtils.OnDialogPressed() {
                    @Override
                    public void onClickAccept() {
                        removeConfirmDialog.dismiss();
                        showProgress();
                        getPresenter().updateReadingStatus(getScreen().editionId(), ReadingState.REMOVE, getScreen().readingId(), getScreen().bookId());
                    }

                    @Override
                    public void onClickRefuse() {
                        // do nothing
                    }
                });
    }

    @OnClick(R.id.image_button_save)
    void onButtonSaveTap () {
        MZDebug.w("___________________________________________________________ onButtonSaveTap __");
        showProgress();
        getPresenter().updateReadingStatus(getScreen().editionId(), mNewReadingState, getScreen().readingId(), getScreen().bookId());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (radioButtonReadingBook.isChecked()) {
            mNewReadingState = ReadingState.READING;
        } else if (radioButtonWantToRead.isChecked()) {
            mNewReadingState = ReadingState.TO_READ;
        } else if (radioButtonRedBook.isChecked()) {
            mNewReadingState = ReadingState.RED;
        }

        updateImageState ();
    }


    private void setChecked (int readingStatus) {
        switch (readingStatus) {
            case ReadingState.TO_READ:
                radioGroup.check(R.id.radio_want_to_read);
                break;

            case ReadingState.READING:
                radioGroup.check(R.id.radio_reading_book);
                break;

            case ReadingState.RED:
                radioGroup.check(R.id.radio_red_book);
                break;
        }
    }

    private void updateImageState () {
        if (mNewReadingState == mReadingState) {
            imageButtonSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_gray, null));
            imageButtonSave.setClickable(false);
            return;
        }

        imageButtonSave.setClickable(true);
        imageButtonSave.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_check_green, null));
    }

    private void onUpdateReadingStatusSuccess(String message) {
        hideProgress();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(BookDetailActivity.KEY_UPDATE_READING_STATUS, mNewReadingState);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void onError (String message) {
        hideProgress();

        errorDialog = ClientUtils.showDialogError(this, getString(R.string.err), message);
    }

    private void onUnAuthorization (String message) {
        unAuthorizationDialog = ClientUtils.showDialogUnAuthorization(this, this, message);
    }


    private void showProgress () {
        loadingDialog.show();
    }

    private void hideProgress () {
        if (loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
    }

}
