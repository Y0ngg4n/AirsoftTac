package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.Result;

import java.nio.charset.StandardCharsets;

import at.favre.lib.crypto.bcrypt.BCrypt;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pro.oblivioncoding.yonggan.airsofttac.AdMob.AdMobIds;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class JoinGameActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    private static final String sharedPrefs = "JoinGameData";
    private static final String gameIDPref = "gameID";
    private static final String nickNamePref = "nickname";
    private static final String passwordPref = "password";

    private boolean allreadyRequestPermission = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_activity);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestCameraPermissions();
        mScannerView = new ZXingScannerView(this);
        findViewById(R.id.scanGameID).setOnClickListener(v -> {
            setContentView(mScannerView);
            mScannerView.startCamera();
            mScannerView.setResultHandler(this::handleResult);
        });

        final SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
        final EditText passwordField = findViewById(R.id.password);
        final TextView nickNameField = findViewById(R.id.nickName);
        final EditText joinGameID = findViewById(R.id.joinGameID);

        passwordField.setText(sharedPreferences.getString(passwordPref, ""));
        nickNameField.setText(sharedPreferences.getString(nickNamePref, ""));
        joinGameID.setText(sharedPreferences.getString(gameIDPref, ""));

        findViewById(R.id.joinGame).setOnClickListener(v -> {
            findViewById(R.id.joinGame).setClickable(false);
            Toast.makeText(this, R.string.code_join_game_activity_trying_to_connect_to_game, Toast.LENGTH_LONG).show();
            FirebaseDB.getGames().whereEqualTo(this.getResources().getString(R.string.firebase_firestore_variable_games_gameID), joinGameID.getText().toString())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        if (!nickNameField.getText().toString().isEmpty()) {
                            final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
                                if (BCrypt.verifyer().verify(passwordField.getText().toString().getBytes(StandardCharsets.UTF_8),
                                        FirebaseDB.getGameData().getPassword().getBytes(StandardCharsets.UTF_8)).verified) {
                                    boolean alreadyExists = false;
                                    if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()) != null)
                                        alreadyExists = true;
                                    if (!alreadyExists) {
                                        FirebaseDB.getGameData().getUsers().add(new UserData(
                                                FirebaseAuthentication.getFirebaseUser().getEmail(), false,
                                                nickNameField.getText().toString()));
                                    } else {
                                        FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail())
                                                .setNickname(nickNameField.getText().toString());
                                    }
                                    final SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(passwordPref, passwordField.getText().toString());
                                    editor.putString(nickNamePref, nickNameField.getText().toString());
                                    editor.putString(gameIDPref, joinGameID.getText().toString());
                                    editor.commit();

                                    FirebaseDB.updateObject(documentSnapshot, this.getResources().getString(R.string.firebase_firestore_variable_games_users),
                                            FirebaseDB.getGameData().getUsers());
                                    JoinGameActivity.this.startActivity(new Intent(JoinGameActivity.this,
                                            MainActivity.class));
                                } else {
                                    findViewById(R.id.joinGame).setClickable(true);
                                    Toast.makeText(getApplicationContext(), R.string.code_join_game_activity_password_wrong, Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            findViewById(R.id.joinGame).setClickable(true);
                            Toast.makeText(getApplicationContext(), R.string.code_join_game_activity_please_enter_nickname,
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        findViewById(R.id.joinGame).setClickable(true);
                        Toast.makeText(getApplicationContext(), R.string.firebase_firestore_could_not_find_document_with_gameid,
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    findViewById(R.id.joinGame).setClickable(true);
                    Toast.makeText(getApplicationContext(), R.string.firebase_firestore_could_not_query_database,
                            Toast.LENGTH_LONG).show();
                }
            });
        });

        findViewById(R.id.switchToCreateGame)
                .setOnClickListener(v -> JoinGameActivity.this.startActivity(new Intent(
                        JoinGameActivity.this, CreateGameActivity.class)));

        final InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(AdMobIds.InterstialAll);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }

        });
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        allreadyRequestPermission = false;
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mScannerView = new ZXingScannerView(this);
        } else {
            requestCameraPermissions();
        }
    }

    private void requestCameraPermissions() {
        if (allreadyRequestPermission) return;
        allreadyRequestPermission = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 3);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(@NonNull final Result rawResult) {
        // Do something with the result here
        setContentView(R.layout.activity_join_game_activity);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((EditText) findViewById(R.id.joinGameID)).setText(rawResult.getText());

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
}
