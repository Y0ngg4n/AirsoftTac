package pro.oblivioncoding.yonggan.airsofttac.CDN;

import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;

import pro.oblivioncoding.yonggan.airsofttac.CDN.Data.ImageViews;
import pro.oblivioncoding.yonggan.airsofttac.CDN.SVG.SvgSoftwareLayerSetter;
import pro.oblivioncoding.yonggan.airsofttac.CDN.URL.ImageURLs;

public class CDNManager {

//    private static final String TAG = "CDNManager";

    private RequestBuilder<PictureDrawable> requestBuilder;
    private Context context;

    public CDNManager(Context context) {
        this.context = context;
        requestBuilder = Glide.with(context).as(PictureDrawable.class)
                .listener(new SvgSoftwareLayerSetter());
    }

    public void loadImages() {
        //TODO: Add Images here
        loadNet(ImageURLs.PLAYER.getUrl(), ImageViews.getInstance().playerIcon);
    }

//    public void clearCache(View v, ImageView[] imageViews) {
//        Log.w(TAG, "clearing cache");
//        RequestManager glideRequests = Glide.with(MainActivity.getInstance());
//        for (ImageView img : imageViews) {
//            glideRequests.clear(img);
//        }
//        Glide.get(MainActivity.getInstance()).clearMemory();
//        File cacheDir = Preconditions.checkNotNull(Glide.getPhotoCacheDir(MainActivity.getInstance()));
//        if (cacheDir.isDirectory()) {
//            for (File child : cacheDir.listFiles()) {
//                if (!child.delete()) {
//                    Log.w(TAG, "cannot delete: " + child);
//                }
//            }
//        }
//    }

    private void loadNet(String url, ImageView imageView) {
        Uri uri = Uri.parse(url);
        RequestBuilder<PictureDrawable> load = requestBuilder.load(uri);

        requestBuilder.load(uri).into(imageView);
    }

}
