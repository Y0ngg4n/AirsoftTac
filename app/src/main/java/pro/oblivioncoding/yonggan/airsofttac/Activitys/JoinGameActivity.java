package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.content.Intent;
import android.os.Bundle;
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
import pro.oblivioncoding.yonggan.airsofttac.R;

public class JoinGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
}
