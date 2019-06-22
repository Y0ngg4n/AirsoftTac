package pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection;

public class MapStyleData {

    private String title, json;

    public MapStyleData() {
    }

    public MapStyleData(final String title, final String json) {
        this.title = title;
        this.json = json;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getJson() {
        return json;
    }

    public void setJson(final String json) {
        this.json = json;
    }
}
