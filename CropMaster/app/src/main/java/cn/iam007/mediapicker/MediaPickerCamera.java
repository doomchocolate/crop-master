package cn.iam007.mediapicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import cn.iam007.crop.master.R;

/**
 * Created by Administrator on 2015/7/7.
 */
public class MediaPickerCamera implements MediaPickerSource {
    private static Uri mUri = null;

    @Override
    public int getName() {
        return R.string.mediapicker_take_from_camera;
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Override
    public void start(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File tmpFile = new File(activity.getExternalCacheDir(), ".camera.tmp");
        mUri = Uri.fromFile(tmpFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, CAPTURE_IMAGE);
    }

    public static Uri getUri(){
        return mUri;
    }
}
