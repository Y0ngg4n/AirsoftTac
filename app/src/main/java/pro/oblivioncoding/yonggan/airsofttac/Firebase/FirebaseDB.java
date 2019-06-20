package pro.oblivioncoding.yonggan.airsofttac.Firebase;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection.KMLData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection.MapStyleData;

public class FirebaseDB {

    @NonNull
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @NonNull
    public static FirebaseFirestore getDb() {
        return db;
    }

    private static GameData gameData;

    private static MapStyleData mapStyleData;

    private static KMLData kmlData;

    @NonNull
    private static CollectionReference games = db.collection("Games");

    private static CollectionReference mapStyles = db.collection("MapStyles");

    private static CollectionReference kml = db.collection("KML");

    public static CollectionReference getMapStyles() {
        return mapStyles;
    }

    public static void setMapStyles(CollectionReference mapStyles) {
        FirebaseDB.mapStyles = mapStyles;
    }

    public static MapStyleData getMapStyleData() {
        return mapStyleData;
    }

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

    public static void setMapStyleData(MapStyleData mapStyleData) {
        FirebaseDB.mapStyleData = mapStyleData;
    }

    public static void setDb(@NonNull FirebaseFirestore db) {
        FirebaseDB.db = db;
    }

    public static KMLData getKmlData() {
        return kmlData;
    }

    public static void setKmlData(KMLData kmlData) {
        FirebaseDB.kmlData = kmlData;
    }

    public static void setGames(@NonNull CollectionReference games) {
        FirebaseDB.games = games;
    }

    public static CollectionReference getKml() {
        return kml;
    }

    public static void setKml(CollectionReference kml) {
        FirebaseDB.kml = kml;
    }
}
