package cn.iam007.mediapicker;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by Administrator on 2015/7/1.
 */
public class MediaPickerGallery implements MediaPickerSource {

    @Override
    public String getName() {
        return "Select a photo from gallery";
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public void start(Activity activity) {
        if (activity != null) {
            Intent localIntent = new Intent(Intent.ACTION_PICK);
            localIntent.setType("image/*");
            localIntent.setAction(Intent.ACTION_GET_CONTENT);
            Intent intent = Intent.createChooser(localIntent, "Gallery Image Picker");

            activity.startActivityForResult(intent, GALLERY_IMAGE);
        }
    }
}
