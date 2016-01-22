package samurai.geeft.android.geeft.util;

import android.net.Uri;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by ugookeadu on 20/01/16.
 */
public class ImageControllerGenerator {
    public static void generateSimpleDrawee(SimpleDraweeView simpleDraweeView,
                                                        String uriString){
        int width = 10;
        int height = 10;
            ViewGroup.LayoutParams lp = simpleDraweeView.getLayoutParams();
            if (lp != null && lp.width > 0) {
               width = lp.width;
            }

            lp = simpleDraweeView.getLayoutParams();
            if (lp != null && lp.height > 0) {
                height = lp.height;
            }


        Uri uri;
        if(uriString==null)
            uriString="";
        uri= Uri.parse(uriString);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri).
                setResizeOptions(new ResizeOptions(width,height)).
                setProgressiveRenderingEnabled(true).build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }
}
