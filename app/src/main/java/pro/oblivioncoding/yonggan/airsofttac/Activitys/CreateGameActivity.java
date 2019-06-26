package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import at.favre.lib.crypto.bcrypt.BCrypt;
import pro.oblivioncoding.yonggan.airsofttac.AdMob.AdMobIds;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.User.UserData;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AddCustomMapDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.Fragments.Dialog.AssignKMLDialogFragment;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class CreateGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game_activity);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        final int mHour = c.get(Calendar.HOUR_OF_DAY);
        final int mMinute = c.get(Calendar.MINUTE);
        final int dHour = c.get(Calendar.HOUR_OF_DAY);
        final int dMinute = c.get(Calendar.MINUTE);

        findViewById(R.id.datePicker).setOnClickListener(v -> {
            new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        ((EditText) findViewById(R.id.date))
                                .setText(year + "-" + String.format("%02d", month + 1)
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

        FirebaseDB.getGames().whereEqualTo("gameID", ((TextView) findViewById(R.id.gameID)).getText().toString())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task == null || task.getResult().size() <= 0) {
                    (findViewById(R.id.createGame)).setOnClickListener(v -> {
                        Toast.makeText(this, "Trying to create Game...", Toast.LENGTH_LONG).show();
                        final GameData gameData = createGame();
                        if (gameData != null) {
                            FirebaseDB.getGames().document().set(gameData).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseDB.setGameData(gameData);
                                    CreateGameActivity.this.startActivity(new Intent(
                                            CreateGameActivity.this, MainActivity.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Couldn´t create Game", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "GameID allready existing!",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Couldn´t query Database!",
                        Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.createKML).setOnClickListener(v -> {
            final AddCustomMapDialogFragment addCustomMapDialogFragment = AddCustomMapDialogFragment.newInstance("new Custom Map");
            addCustomMapDialogFragment.show(getSupportFragmentManager(), "add_custom_map");
        });

        findViewById(R.id.selectKML).setOnClickListener(v -> {
            final AssignKMLDialogFragment assignKMLDialogFragment = AssignKMLDialogFragment.newInstance("Assign KML", this);
            assignKMLDialogFragment.show(getSupportFragmentManager(), "assign_custom_map");
        });


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

    private GameData createGame() {
        try {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final Date startTime = simpleDateFormat.parse(((EditText) findViewById(R.id.date))
                    .getText().toString()
                    + " " + ((EditText) findViewById(R.id.startTime)).getText().toString());
            final Date durationDate = simpleDateFormat.parse(((EditText) findViewById(R.id.date))
                    .getText().toString()
                    + " " + ((EditText) findViewById(R.id.duration)).getText().toString());

            if (durationDate.compareTo(startTime) <= 0) {
                Toast.makeText(this, "Please make shure duration Time is after start time!", Toast.LENGTH_LONG).show();
                return null;
            }

            if (((EditText) findViewById(R.id.nickNameField)).getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill Nickname!", Toast.LENGTH_LONG).show();
                return null;
            }

            final ArrayList<UserData> userData = new ArrayList<UserData>();
            userData.add(new UserData(FirebaseAuthentication.getFirebaseUser().getEmail(),
                    true, ((EditText) findViewById(R.id.nickNameField)).getText().toString()));

            String kmlTitle = ((TextView) findViewById(R.id.kmlTitleLabel)).getText().toString();
            if (kmlTitle == null) kmlTitle = "";

            return new GameData(((EditText) findViewById(R.id.gameID)).getText().toString(),
                    FirebaseAuthentication.getFirebaseUser().getEmail(),
                    new Timestamp(startTime),
                    new Timestamp(durationDate),
                    userData, BCrypt.withDefaults().hashToString(12, ((EditText) findViewById(R.id.passwordField)).getText().toString().toCharArray()),
                    kmlTitle);
        } catch (@NonNull final ParseException e) {
            Toast.makeText(getApplicationContext(), "Could not parse Date", Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private void writeGameData(@NonNull final GameData gameData) {

    }
}
