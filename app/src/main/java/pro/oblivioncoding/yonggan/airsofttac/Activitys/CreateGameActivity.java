package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class CreateGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        int dHour = c.get(Calendar.HOUR_OF_DAY);
        int dMinute = c.get(Calendar.MINUTE);

        findViewById(R.id.datePicker).setOnClickListener(v -> {
            new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        ((EditText) findViewById(R.id.date))
                                .setText(year + "-" + String.format("%02d", month)
                                        + "-" + String.format("%02d", dayOfMonth));
                    }, mYear, mMonth, mDay).show();
        });

        findViewById(R.id.startTimePicker).setOnClickListener(v -> {
            new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> ((EditText) findViewById(R.id.startTime))
                            .setText(String.format("%02d", hourOfDay) + ":"
                                    + String.format("%02d", minute) + ":" + "00"),
                    mHour, mMinute, true).show();
        });

        findViewById(R.id.durationPicker).setOnClickListener(v -> {
            new TimePickerDialog(this,
                    (view, hourOfDay, minute) -> ((EditText) findViewById(R.id.duration))
                            .setText(String.format("%02d", hourOfDay) + ":"
                                    + String.format("%02d", minute) + ":" + "00"),
                    dHour, dMinute, true).show();
        });

        (findViewById(R.id.createGame)).setOnClickListener(v -> {

            FirebaseDB.getGames().whereEqualTo("gameID", ((TextView) findViewById(R.id.gameID)).getText().toString())
                    .get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task == null || task.getResult().size() <= 0) {
                        final GameData gameData = createGame();
                        writeGameData(gameData);
                        FirebaseDB.setGameData(gameData);
                        CreateGameActivity.this.startActivity(new Intent(
                                CreateGameActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "GameID allready existing!",
                                Toast.LENGTH_LONG);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                            Toast.LENGTH_LONG);
                }
            });
        });
    }

    private GameData createGame() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = simpleDateFormat.parse(((EditText) findViewById(R.id.date))
                    .getText().toString()
                    + " " + ((EditText) findViewById(R.id.startTime)).getText().toString());
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date durationDate = simpleDateFormat.parse(((EditText) findViewById(R.id.duration)).getText().toString());
            durationDate = new Date(startTime.getTime() + durationDate.getTime());
            final ArrayList<UserData> userData = new ArrayList<UserData>();
            userData.add(new UserData(FirebaseAuthentication.getFirebaseUser().getEmail(),
                    true));
            return new GameData(((EditText) findViewById(R.id.gameID)).getText().toString(),
                    FirebaseAuthentication.getFirebaseUser().getEmail(),
                    new Timestamp(startTime),
                    new Timestamp(durationDate),
                    userData);

        } catch (ParseException e) {
            Log.i("CreateGame", "Could not parse Date");
            Toast.makeText(getApplicationContext(), "Could not parse Date", Toast.LENGTH_LONG);
        }
        return null;
    }

    private void writeGameData(GameData gameData) {
        FirebaseDB.getGames().document().set(gameData);
    }
}
