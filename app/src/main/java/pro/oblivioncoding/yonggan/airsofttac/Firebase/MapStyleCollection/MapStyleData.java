package pro.oblivioncoding.yonggan.airsofttac.Firebase.MapStyleCollection;

public class MapStyleData {

    private String title, json;

    public MapStyleData() {
    }

    public MapStyleData(String title, String json) {
        this.title = title;
        this.json = json;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
