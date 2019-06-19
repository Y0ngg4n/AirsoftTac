package pro.oblivioncoding.yonggan.airsofttac.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

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

            FirebaseAuthentication.getFirebaseAuth().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FirebaseLogin", "signInWithEmail:success");
                            FirebaseAuthentication.setFirebaseUser(
                                    FirebaseAuthentication.getFirebaseAuth().getCurrentUser());
                            loadScanGameActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FirebaseLogin", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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
