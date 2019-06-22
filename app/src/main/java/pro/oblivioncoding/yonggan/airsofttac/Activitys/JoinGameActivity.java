package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.Manifest;
import android.content.Intent;
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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_activity);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mScannerView = new ZXingScannerView(this);
        findViewById(R.id.scanGameID).setOnClickListener(v -> {
            requestCameraPermissions();
            setContentView(mScannerView);
        });

        findViewById(R.id.joinGame).setOnClickListener(v -> {
            Toast.makeText(this, "Trying to connect to Game...", Toast.LENGTH_LONG).show();
            FirebaseDB.getGames().whereEqualTo("gameID", ((EditText) findViewById(R.id.joinGameID)).getText().toString())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        if (!((TextView) findViewById(R.id.nickName)).getText().toString().isEmpty()) {
                            final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
                                if (BCrypt.verifyer().verify(((EditText) findViewById(R.id.password)).getText().toString().getBytes(StandardCharsets.UTF_8),
                                        FirebaseDB.getGameData().getPassword().getBytes(StandardCharsets.UTF_8)).verified) {
                                    boolean allreadyExists = false;
                                    if (FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail()) != null)
                                        allreadyExists = true;
                                    if (!allreadyExists) {
                                        FirebaseDB.getGameData().getUsers().add(new UserData(
                                                FirebaseAuthentication.getFirebaseUser().getEmail(), false,
                                                ((TextView) findViewById(R.id.nickName)).getText().toString()));

                                    } else {
                                        FirebaseDB.getGameData().getOwnUserData(FirebaseAuthentication.getFirebaseUser().getEmail())
                                                .setNickname(((TextView) findViewById(R.id.nickName)).getText().toString());
                                    }
                                    FirebaseDB.updateObject(documentSnapshot, "users",
                                            FirebaseDB.getGameData().getUsers());
                                    JoinGameActivity.this.startActivity(new Intent(JoinGameActivity.this,
                                            MainActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Password wrong!", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please enter Nickname!",
                                    Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Couldn´t find Document with GameID!",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
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

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            requestCameraPermissions();
        }
    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
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
