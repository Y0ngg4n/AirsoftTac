package pro.oblivioncoding.yonggan.airsofttac.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthentication {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    private static FirebaseUser firebaseUser;

    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public static void setFirebaseUser(FirebaseUser firebaseUser) {
        FirebaseAuthentication.firebaseUser = firebaseUser;
    }

    public static void setFirebaseAuth(FirebaseAuth firebaseAuth) {
        FirebaseAuthentication.firebaseAuth = firebaseAuth;
    }

}
