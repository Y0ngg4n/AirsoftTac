package pro.oblivioncoding.yonggan.airsofttac.CDN.URL;

public enum ImageURLs {
    PLAYER("http://cdn.oblivioncoding.pro/airsofttac/icons/marker/player.svg");

    private String url;

    ImageURLs(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
