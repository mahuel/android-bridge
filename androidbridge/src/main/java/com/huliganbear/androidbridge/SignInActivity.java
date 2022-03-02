package com.huliganbear.androidbridge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.internal.SignInConfiguration;
import com.unity3d.player.UnityPlayer;

public class SignInActivity extends Activity {
    public static int REQUEST_CODE_SIGN_IN = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST123", "activity started starting signin intent");

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.d("TEST123", "signin intent complete");
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null) {
                if (result.isSuccess()) {
                    // The signed in account is stored in the result.
                    Log.d("TEST123", "successful signin intent getting data and stopping this activity");
                    GoogleSignInAccount signedInAccount = result.getSignInAccount();
                    PlayServicesHelper.getInstance().onConnect(signedInAccount, this);
                    finish();
                    return;
                } else {
                    Log.d("TEST123", "sign in status:" + result.getStatus());
                }
            }
            Log.d("TEST123", "unsuccessful signin intent ");
            PlayServicesHelper.getInstance().signInCallback.onSignInFailed();
            finish();
        }
    }
}
