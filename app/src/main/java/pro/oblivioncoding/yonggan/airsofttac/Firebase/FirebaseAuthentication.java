package pro.oblivioncoding.yonggan.airsofttac.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthentication {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseUser firebaseUser;

    public static FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public static void setFirebaseAuth(final FirebaseAuth firebaseAuth) {
        FirebaseAuthentication.firebaseAuth = firebaseAuth;
    }

    public static FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public static void setFirebaseUser(final FirebaseUser firebaseUser) {
        FirebaseAuthentication.firebaseUser = firebaseUser;
    }

}
