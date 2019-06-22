package pro.oblivioncoding.yonggan.airsofttac.Firebase.KMLCollection;

public class KMLData {

    String title, kml;

    public KMLData() {
    }

    public KMLData(final String title, final String kml) {
        this.title = title;
        this.kml = kml;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getKml() {
        return kml;
    }

    public void setKml(final String kml) {
        this.kml = kml;
    }
}
