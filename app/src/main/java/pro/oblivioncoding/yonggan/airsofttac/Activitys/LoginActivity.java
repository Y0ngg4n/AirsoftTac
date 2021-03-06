package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentSnapshot;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseDB;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class LoginActivity extends AppCompatActivity {

    private static final String sharedPrefs = "LoginData";
    private static final String emailPref = "email";
    private static final String passwordPref = "password";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(getApplicationContext());
//        MobileAds.initialize(getApplicationContext(), AdMobIds.AdmobAppID);


        final EditText emailField = findViewById(R.id.email);
        final EditText passwordField = findViewById(R.id.password);
        final SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);

        emailField.setText(sharedPreferences.getString(emailPref, ""));
        passwordField.setText(sharedPreferences.getString(passwordPref, ""));

        findViewById(R.id.login).setOnClickListener(v -> {
            findViewById(R.id.login).setClickable(false);
            final String email = emailField.getText().toString();
            final String password = passwordField.getText().toString();

            if (email.isEmpty() || password.isEmpty()) return;

            FirebaseAuthentication.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            loadScanGameActivity();
                        } else {
                            findViewById(R.id.login).setClickable(true);
                            try {
                                throw task.getException();
                            } catch (final FirebaseAuthUserCollisionException existEmail) {
                                FirebaseAuthentication.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this, task1 -> {
                                            if (task1.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("FirebaseLogin", "signInWithEmail:success");
                                                FirebaseAuthentication.setFirebaseUser(
                                                        FirebaseAuthentication.getFirebaseAuth().getCurrentUser());
                                                final SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString(emailPref, email);
                                                editor.putString(passwordPref, password);
                                                editor.commit();
                                                loadScanGameActivity();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w("FirebaseLogin", "signInWithEmail:failure", task1.getException());
                                                Toast.makeText(getApplicationContext(), R.string.firebase_firebaseauth_authentication_failed,
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (final FirebaseAuthWeakPasswordException weakPassword) {
                                Toast.makeText(getApplicationContext(), R.string.firebase_firebaseauth_password_too_weak, Toast.LENGTH_LONG).show();
                            } catch (final FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Toast.makeText(getApplicationContext(), R.string.firebase_firebaseauth_malformed_email, Toast.LENGTH_LONG).show();
                            } catch (final Exception e) {
                                Log.e("Login", e.getMessage());
                            }
                        }
                    });
        });

        findViewById(R.id.privacyPolicy).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(this.getResources().getString(R.string.code_login_activity_privacy_policy))));
        });

        findViewById(R.id.termsandConditions).setOnClickListener(v -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(this.getResources().getString(R.string.code_login_activity_terms_and_conditions))));
        });

        FirebaseDB.getGames().whereLessThanOrEqualTo(this.getResources().getString(R.string.firebase_firestore_variable_games_endTime), Timestamp.now()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() > 0) {
                    for (final DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        documentSnapshot.getReference().delete();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void loadScanGameActivity() {
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, JoinGameActivity.class));
    }
}
