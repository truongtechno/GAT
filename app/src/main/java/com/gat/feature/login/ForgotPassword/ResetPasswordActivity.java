package com.gat.feature.login.ForgotPassword;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.CommonCheck;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.response.impl.LoginResponseData;
import com.gat.feature.login.LoginPresenter;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.main.MainActivity;
import com.gat.feature.main.MainScreen;
import com.gat.feature.search.SearchActivity;
import com.gat.feature.search.SearchScreen;

import butterknife.BindView;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 3/3/17.
 */

public class ResetPasswordActivity extends ScreenActivity<LoginScreen, LoginPresenter> {
    @BindView(R.id.text_input)
    EditText passwordText;

    @BindView(R.id.textEmail)
    TextView emailTextView;

    @BindView(R.id.btn_send)
    Button btnVerify;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.layoutMenutop)
    RelativeLayout headerLayout;


    CompositeDisposable disposable;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);

        emailTextView.setText(getScreen().email());

        headerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.background_header_blue, null));
        txtTitle.setText(getString(R.string.forgot_password));
        txtTitle.setAllCaps(true);
        txtTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
        imgBack.setVisibility(View.GONE);
        imgSave.setVisibility(View.GONE);


        disposable = new CompositeDisposable(
                getPresenter().changePasswordResult().subscribe(this::onSuccess),
                getPresenter().onError().subscribe(this::onError)
        );

        progressDialog = new ProgressDialog(this);

        btnVerify.setOnClickListener(view -> {
            String password = passwordText.getText().toString();
            Pair<Boolean, Integer> result = CommonCheck.validatePassword(password);
            if (!result.first) {
                String error;
                if (result.second == CommonCheck.Error.FIELD_EMPTY) {
                    error = getString(R.string.password_invalid);
                } else if (result.second == CommonCheck.Error.EMAIL_INVALID) {
                    error = getString(R.string.password_invalid);
                } else {
                    error = Strings.EMPTY;
                }
                passwordText.setError(error);
                return;
            }
            onResetPassword(true);
            getPresenter().changePassword(password);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }

    @Override
    protected LoginScreen getDefaultScreen() {
        return LoginScreen.instance(Strings.EMPTY);
    }

    public void onError(String error) {
        onResetPassword(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    public void onSuccess(ServerResponse<LoginResponseData> responseDataServerResponse) {
        onResetPassword(false);
        this.start(getApplicationContext(), MainActivity.class, MainScreen.instance());
        finish();
    }
    @Override
    protected int getLayoutResource() {
        return R.layout.reset_pass_activity;
    }

    @Override
    protected Class<LoginPresenter> getPresenterClass() {
        return LoginPresenter.class;
    }

    private void onResetPassword(boolean enter) {
        if (enter) {
            Views.hideKeyboard(this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.pass_reseting));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }
}
