package com.gat.common.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.app.fragment.ScreenFragment;
import com.gat.feature.book_detail.BookDetailActivity;
import com.gat.feature.book_detail.add_to_bookcase.AddToBookcaseActivity;
import com.gat.feature.login.LoginScreen;
import com.gat.feature.start.StartActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by truongtechno on 29/03/2017.
 */

public class ClientUtils {

    public final static String SIZE_DEFAULT = "o";
    public final static String SIZE_THUMBNAIL = "t";
    public final static String SIZE_SMALL = "s";
    public final static String SIZE_LARGE = "q";

    public static Context context; // util ma dat bien -> de bi chet lam nahh..
    private static final String DEFAULT_IMAGE = "33328625223";       // TODO default image path


    public static void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.show();
    }

    public static String getUrlImage(String image, String size) {
        return Constance.BASE_URL_IMAGE + "common/get_image/" + (Strings.isNullOrEmpty(image) ? DEFAULT_IMAGE : image) + "?size=" + size;
    }

//    public static void setImage(ImageView image, int drawble, String url) {
//        if (image != null) {
//            Glide.with(context).load(url).placeholder(drawble).error(drawble).dontAnimate().into(image);
//        }
//    }

    public static void setImage(Context context, ImageView image, int drawble, String url) {
        if (image != null) {
            Glide.with(context).load(url).placeholder(drawble).error(drawble).dontAnimate().into(image);
        }
    }

    public static void setImage(ImageView image, String url) {
        if (image != null) {
            Glide.with(context).load(url).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).fitCenter().into(image);
        }
    }


    public static String formatColor(String input, String color) {
        return "<font color=\"" + color + "\">" + input + "</font>";
    }

    public static String formatColorAndSize(String input, String color, int size) {
        return "<font size=\"" + size + "\" color=\"" + color + "\">" + input + "</font>";
    }


    public static String getDateFromString(long input) {
        Date date = new Date(input + 1000);
        SimpleDateFormat formatBack = new SimpleDateFormat("dd-MM-yyyy");
        try {
            String dateConvert = formatBack.format(date);
            return dateConvert;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap loadBitmap(ImageView imageView, String url, OnBitmapLoaded onBitmapLoaded) {
        Bitmap bitmap = null;
        Glide.with(context)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(imageView.getWidth(), imageView.getHeight()) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageView.setImageBitmap(resource);
                        onBitmapLoaded.onBitmapLoaded(resource);
                    }
                });

        return bitmap;
    }


    public interface OnBitmapLoaded {
        void onBitmapLoaded(Bitmap bitmap);
    }

    public static
    @Nullable
    Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public static String imageEncode64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }

    public static AlertDialog showErrorDialog(String header, String content, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.error_popup_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textHeader = (TextView) view.findViewById(R.id.error_header);
        textHeader.setText(header);
        TextView textContent = (TextView) view.findViewById(R.id.error_text);
        textContent.setText(content);
        Button button = (Button) view.findViewById(R.id.btn_popup_ok);
        button.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
        return dialog;
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void showViewNotInternet(Context context, View view) {
        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View viewInternet = inflater.inflate(R.layout.layout_intenet_notconnect, null);
            PopupWindow windowIntenet = new PopupWindow(viewInternet, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            windowIntenet.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            windowIntenet.setBackgroundDrawable(new BitmapDrawable());
            windowIntenet.setOutsideTouchable(false);
            windowIntenet.showAsDropDown(view, 0, 0);

            new Handler().postDelayed(() -> windowIntenet.dismiss(),5000);

        } catch (Exception e) {
        }
    }

    public static void hideViewNotInternet(View view) {
        ViewGroup viewGroup = (ViewGroup) view;
        if (viewGroup.getChildCount() > 0) {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View view1 = viewGroup.getChildAt(i);
//                if (view1.getId() == Constance.ID_VIEW_NETWORK) {
//                    view1.setVisibility(View.GONE);
//                } else {
//                    view1.setVisibility(View.VISIBLE);
//                }
            }
        }
    }

    public static String getStringLanguage(int i) {
        return context.getResources().getString(i);
    }

    public static int roundDouble(double d) {
        double dAbs = Math.abs(d);
        int i = (int) dAbs;
        double result = dAbs - (double) i;
        if (result < 0.5) {
            return d < 0 ? -i : i;
        } else {
            return d < 0 ? -(i + 1) : i + 1;
        }
    }

    public static AlertDialog createLoadingDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_progress_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_loading);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(context).load(R.raw.ic_loading_book).into(imageViewTarget);

        return dialog;
    }


    public interface OnDialogPressed {
        void onClickAccept();

        void onClickRefuse();
    }

    public static AlertDialog showAlertDialog(@NonNull Context context, @NonNull String title, @Nullable String message,
                                              @Nullable String accept_text, @Nullable String refuse_text,
                                              @Nullable OnDialogPressed callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.custom_alert_dialog, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        if (message == null) {
            TextView textHeader = (TextView) view.findViewById(R.id.textViewTitle);
            textHeader.setText(title);
        } else {
            TextView textHeader = (TextView) view.findViewById(R.id.textViewTitle);
            textHeader.setText(title);

            TextView textContent = (TextView) view.findViewById(R.id.textViewMessage);
            textContent.setText(message);
        }

        Button buttonAccept = (Button) view.findViewById(R.id.buttonAccept);
        Button buttonRefuse = (Button) view.findViewById(R.id.buttonRefuse);

        if (!TextUtils.isEmpty(accept_text)) {
            buttonAccept.setText(accept_text);
        }
        if (!TextUtils.isEmpty(refuse_text)) {
            buttonRefuse.setText(refuse_text);
        }

        buttonAccept.setOnClickListener(v -> {
            callback.onClickAccept();
        });

        buttonRefuse.setOnClickListener(v -> {
            dialog.dismiss();
            callback.onClickRefuse();
        });

        dialog.show();
        return dialog;
    }

    public static <T extends ScreenActivity> AlertDialog showRequiredLoginDialog(Activity activity, T screen) {
        return showAlertDialog(activity, activity.getString(R.string.err_notice),
                activity.getString(R.string.err_required_login),
                activity.getString(R.string.login),
                activity.getString(R.string.dont_care), new ClientUtils.OnDialogPressed() {
                    @Override
                    public void onClickAccept() {
                        // .start not clear back stack -> bug (can not start next time),
                        // -> resolved by use .startAndClear
                        screen.startAndClear(activity.getApplicationContext(), StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
                    }

                    @Override
                    public void onClickRefuse() {
                        // do nothing

                    }
                });
    }

    public static AlertDialog showChangedValueDialog (Activity activity) {
        return showAlertDialog(activity,
                activity.getString(R.string.err_discard_title),
                activity.getString(R.string.err_discard_message),
                null, null, new ClientUtils.OnDialogPressed() {
                    @Override
                    public void onClickAccept() {
                        activity.finish();
                    }

                    @Override
                    public void onClickRefuse() {
                        // do nothing
                    }
                });
    }

    public static <T extends ScreenActivity> AlertDialog showDialogUnAuthorization(Activity activity, T screen, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_dialog_error, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTopTitle);
        TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
        Button btnAgreed = (Button) view.findViewById(R.id.btnAgreed);

        txtTitle.setText(screen.getResources().getString(R.string.err));
        txtContent.setText(message);
        btnAgreed.setOnClickListener(v -> {
            dialog.dismiss();
            screen.startAndClear(activity.getApplicationContext(), StartActivity.class, LoginScreen.instance(Strings.EMPTY, true));
        });

        dialog.show();

        return dialog;
    }


    public static AlertDialog showDialogError(@NonNull Activity activity, @NonNull String title, @NonNull String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View view = layoutInflater.inflate(R.layout.layout_dialog_error, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTopTitle);
        TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
        Button btnAgreed = (Button) view.findViewById(R.id.btnAgreed);

        txtTitle.setText(title);
        txtContent.setText(message);
        btnAgreed.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnKeyListener((dialog1, keyCode, event) -> {
            if ((keyCode ==  android.view.KeyEvent.KEYCODE_BACK)) {
                dialog.dismiss();
                return true; // pretend we've processed it
            }
            else
                return false; // pass on to be processed as normal
        });


        dialog.show();

        return dialog;
    }


    public static AlertDialog showDialogError409(@NonNull Context context,
                                                @NonNull String title, @NonNull String message,
                                                @Nullable OnDialogPressed callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.layout_dialog_error, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView txtTitle = (TextView) view.findViewById(R.id.txtTopTitle);
        TextView txtContent = (TextView) view.findViewById(R.id.txtContent);
        Button btnAgreed = (Button) view.findViewById(R.id.btnAgreed);

        txtTitle.setText(title);
        txtContent.setText(message);
        btnAgreed.setOnClickListener(v -> {
            dialog.dismiss();

            if (callback != null) {
                callback.onClickAccept();
            }

        });

        dialog.show();

        return dialog;
    }


    public static String displayMeter (float meters) {

        if (meters < 1000) {

            return Math.round(meters) + " m";
        }

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(meters/1000) + " km";
    }

}
