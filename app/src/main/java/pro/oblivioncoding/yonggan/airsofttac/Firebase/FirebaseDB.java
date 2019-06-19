package pro.oblivioncoding.yonggan.airsofttac.Firebase;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;

public class FirebaseDB {

    @NonNull
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @NonNull
    public static FirebaseFirestore getDb() {
        return db;
    }

    private static GameData gameData;

    @NonNull
    private static CollectionReference games = db.collection("Games");

    public static GameData getGameData() {
        return gameData;
    }

    @NonNull
    public static CollectionReference getGames() {
        return games;
    }

    public static void setGameData(final GameData gameData) {
        FirebaseDB.gameData = gameData;
    }

    public static void updateObject(final DocumentSnapshot documentSnapshot, @NonNull final String path, final Object object) {
        FirebaseDB.getGames().document(documentSnapshot.getId()).update(path, object);
    }

    public static void updateObject(final DocumentReference documentReference, @NonNull final String path, final Object object) {
        documentReference.update(path, object);
    }

    public static void updateObject(final DocumentReference documentReference, final Object object) {
        documentReference.set(object);
    }

}
