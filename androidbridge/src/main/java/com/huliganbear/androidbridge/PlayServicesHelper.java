package com.huliganbear.androidbridge;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.huliganbear.androidbridge.callbacks.SignInCallback;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class PlayServicesHelper {

    private GoogleSignInAccount googleSignInAccount;
    private SnapshotsClient snapshotsClient;

    public SignInCallback signInCallback;

    private static final String FILE_NAME = "space_farm_save_data";

    private static PlayServicesHelper instance = null;

    private PlayServicesHelper() {
    }

    public static PlayServicesHelper getInstance() {
        if (instance == null) {
            instance = new PlayServicesHelper();
        }

        return instance;
    }


    public void signIn(Activity activity, SignInCallback signInCallback) {
        Log.d("TEST123", "starting sign in method");
        GoogleSignInOptions signInOption =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        .requestScopes(Games.SCOPE_GAMES_SNAPSHOTS, Games.SCOPE_GAMES_LITE)
                        .build();
        Log.d("TEST123", "created options");
        GoogleSignInClient signInClient = GoogleSignIn.getClient(activity, signInOption);
        Log.d("TEST123", "created signIn client");
        this.signInCallback = signInCallback;
        signInClient.silentSignIn().addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
            @Override
            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                if (task.isSuccessful()) {
                    Log.d("TEST123", "onComplete sign in successful");
                    onConnect(task.getResult(), activity);
                } else {
                    Log.d("TEST123", "onComplete sign in unsuccessful", task.getException());
                    startSignInIntent(activity);
                    // Player will need to sign-in explicitly using via UI
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEST123", "onFailure sign in unsuccessful");
                signInCallback.onSignInFailed();
            }
        });
    }

    private void startSignInIntent(Activity activity) {
        Log.d("TEST123", "starting activity");
        activity.startActivity(new Intent(activity, SignInActivity.class));
    }


    public void onConnect(GoogleSignInAccount googleSignInAccount, Activity activity) {
        if (this.googleSignInAccount != googleSignInAccount) {
            this.googleSignInAccount = googleSignInAccount;
            this.snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount);
        }

        Log.d("TEST123", "starting loading game");
        loadGame(signInCallback);
    }

    public void saveGame(String data, Activity activity) {
        Log.d("TEST123", "getting snapshot client");
        this.snapshotsClient =
                Games.getSnapshotsClient(activity, Objects.requireNonNull(GoogleSignIn.getLastSignedInAccount(activity)));
        Log.d("TEST123", "generating openTask");
        Task<SnapshotsClient.DataOrConflict<Snapshot>> openTask = this.snapshotsClient.open(FILE_NAME, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED);
        openTask.addOnSuccessListener(new OnSuccessListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
            @Override
            public void onSuccess(SnapshotsClient.DataOrConflict<Snapshot> snapshotDataOrConflict) {
                Log.d("TEST123", "open task onSuccess");
                Snapshot snapshot = snapshotDataOrConflict.getData();
                if (snapshot != null) {
                    Log.d("TEST123", "snapshot not null");
                    writeToSnapshotAndSave(snapshot, data);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEST123", "open task failed", e);
            }
        });
    }

    private void loadGame(SignInCallback signInCallback) {
        Task<SnapshotsClient.DataOrConflict<Snapshot>> openTask = this.snapshotsClient.open(FILE_NAME, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED);
        openTask.addOnSuccessListener(new OnSuccessListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
            @Override
            public void onSuccess(SnapshotsClient.DataOrConflict<Snapshot> snapshotDataOrConflict) {

                try {
                    byte[] rawData = snapshotDataOrConflict.getData().getSnapshotContents().readFully();
                    if (rawData == null) {
                        Log.d("TEST123", "loadGame onSuccess no data found");
                        signInCallback.onSignInSuccess("");
                    } else {
                        Log.d("TEST123", "loadGame onSuccess data found!");
                        signInCallback.onSignInSuccess(new String(rawData));
                    }

                } catch (IOException e) {
                    Log.d("TEST123", "loadGame onSuccess exception");
                    signInCallback.onSignInFailed();
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TEST123", "loadGame onFailure");
                signInCallback.onSignInFailed();
            }
        });
    }

    private void writeToSnapshotAndSave(Snapshot snapshot, String data) {
        Log.d("TEST123", "writing to snapshot");
        snapshot.getSnapshotContents().writeBytes(data.getBytes());
        Log.d("TEST123", "creating metadata change for snapshot");
        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription(new Date().toString() + "save data")
                .build();
        Log.d("TEST123", "commit and close for snapshot");
        snapshotsClient.commitAndClose(snapshot, metadataChange);
    }

}
