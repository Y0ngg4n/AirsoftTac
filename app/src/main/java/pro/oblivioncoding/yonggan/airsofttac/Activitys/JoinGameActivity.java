package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.QR.helpers.CameraFace;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.ScanGameIDDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class JoinGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.scanGameID).setOnClickListener(v -> {
            ScanGameIDDialogFragment scanGameIDDialogFragment = ScanGameIDDialogFragment.newInstance("Scan GameID", findViewById(R.id.joinGameID), CameraFace.BACK, this);
            scanGameIDDialogFragment.show(getSupportFragmentManager(), "scan_gameID");
        });

        findViewById(R.id.joinGame).setOnClickListener(v -> {
            FirebaseDB.getGames().whereEqualTo("gameID", ((EditText) findViewById(R.id.joinGameID)).getText().toString())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        if (!((TextView) findViewById(R.id.nickName)).getText().toString().isEmpty()) {
                            final DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);

                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                FirebaseDB.setGameData(documentSnapshot.toObject(GameData.class));
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
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
}
