package pro.oblivioncoding.yonggan.airsofttac.CDN.BMP;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.widget.ImageView;

public class BitmapDecoder {

    public static Bitmap BitmapFromImageView(ImageView imageView) {
        return ((BitmapDrawable) ((LayerDrawable) imageView.getDrawable()).getDrawable(0)).getBitmap();
    }
}
