package samurai.geeft.android.geeft.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import samurai.geeft.android.geeft.R;

/**
 * Created by ugookeadu on 20/01/16.
 * class that set the image default setting for SimpleDrawee
 */

public class ImageControllerGenerator {
    public static void generateSimpleDrawee(Context context,SimpleDraweeView simpleDraweeView,
                                                        String uriString){
        int width = 1;
        int height = 1;
        ViewGroup.LayoutParams lp = simpleDraweeView.getLayoutParams();
        Log.d("SIZE",lp.height+" "+lp.width);
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
        /*
        Postprocessor redMeshPostprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "redMeshPostprocessor";
            }

            @Override
            public CloseableReference<Bitmap> process(
                    Bitmap sourceBitmap,
                    PlatformBitmapFactory bitmapFactory) {
                CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                        sourceBitmap.getWidth() ,
                        sourceBitmap.getHeight() );
                try {
                    Bitmap destBitmap = bitmapRef.get();
                    for (int x = 0; x < destBitmap.getWidth(); x++) {
                        for (int y = 0; y < destBitmap.getHeight(); y++) {
                            destBitmap.setPixel(x, y, sourceBitmap.getPixel(x, y));
                        }
                    }
                    return CloseableReference.cloneOrNull(bitmapRef);
                } finally {
                    CloseableReference.closeSafely(bitmapRef);
                }
            }
        };*/

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setRetainImageOnFailure(true)
                .setOldController(simpleDraweeView.getController())
                .setTapToRetryEnabled(true)
                .build();
        GenericDraweeHierarchyBuilder builder =
                new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setPlaceholderImage(context.getResources().getDrawable(R.drawable.ic_image_placeholder))
                .build();
        simpleDraweeView.setController(controller);
        simpleDraweeView.setHierarchy(hierarchy);
    }

}
