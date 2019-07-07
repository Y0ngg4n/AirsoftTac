package pro.oblivioncoding.yonggan.airsofttac.Firebase;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.GameData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection.KMLData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection.MapStyleData;
import pro.oblivioncoding.yonggan.airsofttac.Firebase.OverlayImageCollection.OverlayImage;

public class FirebaseDB {

    @NonNull
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static GameData gameData;
    private static MapStyleData mapStyleData;
    private static KMLData kmlData;
    private static OverlayImage overlayImage;

    @NonNull
    private static CollectionReference games = db.collection("Games");
    private static CollectionReference mapStyles = db.collection("MapStyles");
    private static CollectionReference kml = db.collection("KML");
    private static CollectionReference overlayImages = db.collection("OverlayImages");

    @NonNull
    public static FirebaseFirestore getDb() {
        return db;
    }

    public static void setDb(@NonNull final FirebaseFirestore db) {
        FirebaseDB.db = db;
    }

    public static CollectionReference getMapStyles() {
        return mapStyles;
    }

    public static void setMapStyles(final CollectionReference mapStyles) {
        FirebaseDB.mapStyles = mapStyles;
    }

    public static MapStyleData getMapStyleData() {
        return mapStyleData;
    }

    public static void setMapStyleData(final MapStyleData mapStyleData) {
        FirebaseDB.mapStyleData = mapStyleData;
    }

    public static GameData getGameData() {
        return gameData;
    }

    public static void setGameData(final GameData gameData) {
        FirebaseDB.gameData = gameData;
    }

    @NonNull
    public static CollectionReference getGames() {
        return games;
    }

    public static void setGames(@NonNull final CollectionReference games) {
        FirebaseDB.games = games;
    }

    public static void updateObject(final DocumentSnapshot documentSnapshot, @NonNull final String path, final Object object) {
        FirebaseDB.getGames().document(documentSnapshot.getId()).update(path, object);
    }

    public static void updateObject(final DocumentReference documentReference, @NonNull final String path, final Object object) {
        documentReference.update(path, object);
    }

    public static void updateObject(final DocumentReference documentReference, @NonNull final Object object) {
        documentReference.set(object);
    }

    public static KMLData getKmlData() {
        return kmlData;
    }

    public static void setKmlData(final KMLData kmlData) {
        FirebaseDB.kmlData = kmlData;
    }

    public static CollectionReference getKml() {
        return kml;
    }

    public static void setKml(final CollectionReference kml) {
        FirebaseDB.kml = kml;
    }

    public static OverlayImage getOverlayImage() {
        return overlayImage;
    }

    public static void setOverlayImage(OverlayImage overlayImage) {
        FirebaseDB.overlayImage = overlayImage;
    }

    public static CollectionReference getOverlayImages() {
        return overlayImages;
    }

    public static void setOverlayImages(CollectionReference overlayImages) {
        FirebaseDB.overlayImages = overlayImages;
    }
}
