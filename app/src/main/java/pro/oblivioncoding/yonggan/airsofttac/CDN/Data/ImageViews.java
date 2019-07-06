package pro.oblivioncoding.yonggan.airsofttac.CDN.Data;

import android.content.Context;
import android.widget.ImageView;

public class ImageViews {

    public static ImageViews instance;
    public Context context;
    public ImageView playerIcon = new ImageView(context);

    public ImageViews(Context context) {
        context = context;
        instance = this;
    }

    public static ImageViews getInstance() {
        return instance;
    }

}
