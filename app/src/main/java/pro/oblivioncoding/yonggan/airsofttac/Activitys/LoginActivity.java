package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.FirebaseAuthentication;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseApp.initializeApp(getApplicationContext());
        findViewById(R.id.login).setOnClickListener(v -> {
            final String email = ((EditText) findViewById(R.id.email)).getText().toString();
            final String password = ((EditText) findViewById(R.id.password)).getText().toString();

            if (email.isEmpty() || password.isEmpty()) return;

            FirebaseAuthentication.getFirebaseAuth().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            loadScanGameActivity();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                FirebaseAuthentication.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this, task1 -> {
                                            if (task1.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d("FirebaseLogin", "signInWithEmail:success");
                                                FirebaseAuthentication.setFirebaseUser(
                                                        FirebaseAuthentication.getFirebaseAuth().getCurrentUser());
                                                loadScanGameActivity();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w("FirebaseLogin", "signInWithEmail:failure", task1.getException());
                                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                Toast.makeText(getApplicationContext(), "Password is too weak!", Toast.LENGTH_LONG).show();
                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                Toast.makeText(getApplicationContext(), "Malformed Email!", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Log.i("LoginLogin", e.getMessage());
                            }
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
    }

    private void loadScanGameActivity() {
        LoginActivity.this.startActivity(new Intent(LoginActivity.this, JoinGameActivity.class));
    }
}
