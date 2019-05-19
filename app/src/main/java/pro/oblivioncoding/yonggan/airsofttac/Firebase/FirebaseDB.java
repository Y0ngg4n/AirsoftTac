package pro.oblivioncoding.yonggan.airsofttac.Firebase;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.function.Consumer;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.R;

public class FirebaseDB {

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseFirestore getDb() {
        return db;
    }

    private static GameData gameData;

    private static CollectionReference games = db.collection("Games");

    public static GameData getGameData() {
        return gameData;
    }

    public static CollectionReference getGames() {
        return games;
    }

    public static void setGameData(GameData gameData) {
        FirebaseDB.gameData = gameData;
    }

    public static void updateObject(DocumentSnapshot documentSnapshot, String path, Object object){
        FirebaseDB.getGames().document(documentSnapshot.getId()).update(path, object);
    }

    public static void updateObject(DocumentReference documentReference, String path, Object object){
        documentReference.update(path, object);
    }

}
