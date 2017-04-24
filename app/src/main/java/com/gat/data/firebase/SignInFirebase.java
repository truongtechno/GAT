package com.gat.data.firebase;

import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Observable;

/**
 * Created by ducbtsn on 4/4/17.
 */

public interface SignInFirebase {
    public void login();
    public void signOut();
    public void register();

    public Observable<FirebaseUser> getLoginResult();

    public void destroy();
}
