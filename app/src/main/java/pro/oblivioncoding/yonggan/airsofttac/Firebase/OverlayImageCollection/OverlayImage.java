package pro.oblivioncoding.yonggan.airsofttac.Firebase.OverlayImageCollection;

public class OverlayImage {

    private String name, image;
    private double latitude, longitude;
    private float width;

    public OverlayImage() {
    }

    public OverlayImage(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public OverlayImage(String name, String image, double latitude, double longitude, float width) {
        this.name = name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.width = width;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
