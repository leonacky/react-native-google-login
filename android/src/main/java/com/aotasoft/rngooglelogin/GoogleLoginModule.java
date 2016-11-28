package com.aotasoft.rngooglelogin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

/**
 * Created by leonacky on 11/24/16.
 */

public class GoogleLoginModule extends ReactContextBaseJavaModule implements ActivityEventListener, GoogleApiClient.OnConnectionFailedListener {

    private ReactApplicationContext mReactContext;
    private final String CALLBACK_TYPE_SUCCESS = "success";
    private final String CALLBACK_TYPE_ERROR = "error";
    private final String CALLBACK_TYPE_CANCEL = "cancel";
    int REQUEST_LOGIN_GOOGLE = 0x99;
    Callback mTokenCallback;
    GoogleApiClient mGoogleApiClient;

    String TAG = "GoogleLoginModule";
    String rn_google_server_key = "";

    public GoogleLoginModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
        reactContext.addActivityEventListener(this);
        initGoogle();
    }

    void initGoogle() {
        try {
            ApplicationInfo ai = mReactContext.getPackageManager().getApplicationInfo(mReactContext.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            rn_google_server_key = bundle.getString("rn_google_server_key");
            Log.i(TAG, "KEY="+rn_google_server_key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient == null) {
            GoogleSignInOptions gso = null;

            if (rn_google_server_key == null || rn_google_server_key.equals("")) {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(Scopes.PROFILE))
                        .requestEmail()
                        .build();
            } else {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(new Scope(Scopes.PROFILE))
                        .requestIdToken(rn_google_server_key)
                        .requestEmail()
                        .build();
            }
            mGoogleApiClient = new GoogleApiClient.Builder(mReactContext)
//                    .enableAutoManage(mReactContext, GoogleLoginModule.this)
                    .addOnConnectionFailedListener(GoogleLoginModule.this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }
    }

    @Override
    public String getName() {
        return "RNGoogleLogin";
    }

    private void consumeCallback(String type, WritableMap map) {
        if (mTokenCallback != null) {
            map.putString("type", type);
            map.putString("provider", "google");
            if (type.equals(CALLBACK_TYPE_SUCCESS)) {
                mTokenCallback.invoke(null, map);
            } else {
                mTokenCallback.invoke(map, null);
            }
            mTokenCallback = null;
        }
    }

    @ReactMethod
    public void login(final Callback callback) {
        this.mTokenCallback = callback;
        if (mGoogleApiClient != null) {
            try {
                if (mGoogleApiClient.isConnected())
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
            } catch (Exception e) {
            }
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mReactContext.startActivityForResult(signInIntent, REQUEST_LOGIN_GOOGLE, null);
        }
    }

    @ReactMethod
    public void logout() {
        try {
            if (mGoogleApiClient.isConnected())
                mGoogleApiClient.clearDefaultAccountAndReconnect();
        } catch (Exception e) {
        }
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        onActivityResult(requestCode, resultCode, data);
    }

    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent intent) {
        if (requestCode == REQUEST_LOGIN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            Log.d(TAG, "result: " + result.isSuccess());
            try {
                WritableMap map = Arguments.createMap();
                if (result.isSuccess()) {
                    final GoogleSignInAccount acct = result.getSignInAccount();
                    WritableMap profile = Arguments.createMap();
                    profile.putString("id", acct.getId());
                    profile.putString("name", acct.getDisplayName());
                    profile.putString("email", acct.getEmail());
                    profile.putString("photo", acct.getPhotoUrl().toString());
                    map.putMap("profile", profile);

                    WritableMap credentials = Arguments.createMap();
                    credentials.putString("userId", acct.getId());
                    if (rn_google_server_key != null && !rn_google_server_key.equals(""))
                        credentials.putString("token", acct.getIdToken());
                    credentials.putString("serverAuthCode", acct.getServerAuthCode());
                    map.putMap("credentials", credentials);
                    map.putString("eventName", "onLogin");
                    consumeCallback(CALLBACK_TYPE_SUCCESS, map);
                } else {
                    if (result.getStatus().isCanceled()) {
                        map.putString("code", result.getStatus().toString());
                        map.putString("eventName", "onCancel");
                        consumeCallback(CALLBACK_TYPE_CANCEL, map);
                    } else {
                        map.putString("code", result.getStatus().toString());
                        map.putString("eventName", "onError");
                        consumeCallback(CALLBACK_TYPE_ERROR, map);
                    }
                }
                return;
            } catch (Throwable e) {
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    
    public void onNewIntent(Intent intent) {

    }
}
