package com.huliganbear.androidbridge;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.SnapshotsClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.huliganbear.androidbridge.callbacks.LoadCallback;
import com.huliganbear.androidbridge.callbacks.SignInCallback;

import java.io.IOException;
import java.util.Date;

public class PlayServicesHelper {

    private GoogleSignInAccount googleSignInAccount;
    private SnapshotsClient snapshotsClient;

    private static final String FILE_NAME = "space_farm_save_data";

    public void signIn(Activity activity, SignInCallback signInCallback) {
        GoogleSignInOptions signInOption =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                        .requestScopes(Games.SCOPE_GAMES_SNAPSHOTS, Games.SCOPE_GAMES_LITE)
                        .build();

        GoogleSignInClient signInClient = GoogleSignIn.getClient(activity, signInOption);
        signInClient.silentSignIn().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onConnect(task.getResult(), activity);

                signInCallback.onSignInSuccess();
            } else {
                signInCallback.onSignInFailed();
                // Player will need to sign-in explicitly using via UI
            }
        });
    }

    private void onConnect(GoogleSignInAccount googleSignInAccount, Activity activity) {
        if (this.googleSignInAccount != googleSignInAccount) {
            this.googleSignInAccount = googleSignInAccount;
            this.snapshotsClient = Games.getSnapshotsClient(activity, googleSignInAccount);
        }
    }

    public void saveGame(String data) {
        Task<SnapshotsClient.DataOrConflict<Snapshot>> openTask = this.snapshotsClient.open(FILE_NAME, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED);
        openTask.addOnSuccessListener(new OnSuccessListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
            @Override
            public void onSuccess(SnapshotsClient.DataOrConflict<Snapshot> snapshotDataOrConflict) {
                Snapshot snapshot = snapshotDataOrConflict.getData();
                writeToSnapshotAndSave(snapshot, data);
            }
        });
    }

    public void loadGame(LoadCallback loadCallback) {
        Task<SnapshotsClient.DataOrConflict<Snapshot>> openTask = this.snapshotsClient.open(FILE_NAME, true, SnapshotsClient.RESOLUTION_POLICY_MOST_RECENTLY_MODIFIED);
        openTask.addOnSuccessListener(new OnSuccessListener<SnapshotsClient.DataOrConflict<Snapshot>>() {
            @Override
            public void onSuccess(SnapshotsClient.DataOrConflict<Snapshot> snapshotDataOrConflict) {
                try {
                    byte[] rawData = snapshotDataOrConflict.getData().getSnapshotContents().readFully();
                    loadCallback.onLoadSuccess(new String(rawData));
                } catch (IOException e) {
                    loadCallback.onLoadFail();
                    e.printStackTrace();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadCallback.onLoadFail();
            }
        });
    }

    private void writeToSnapshotAndSave(Snapshot snapshot, String data) {
        snapshot.getSnapshotContents().writeBytes(data.getBytes());

        SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
                .setDescription(new Date().toString() + "save data")
                .build();

        snapshotsClient.commitAndClose(snapshot, metadataChange);
    }

}
