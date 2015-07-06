package cn.iam007.mediapicker;

import android.app.Activity;

/**
 * Created by Administrator on 2015/7/1.
 */
public interface MediaPickerSource {
    public final static int GALLERY_IMAGE = 0x0F01;
    public final static int GALLERY_VIDEO = 0x0F02;
    public final static int CAPTURE_IMAGE = 0x0F03;
    public final static int CAPTURE_VIDEO = 0x0F04;

    /**
     * 获取媒体源名
     *
     * @return 返回媒体源名
     */
    String getName();

    /**
     * 获取用于表示该媒体源的图标
     *
     * @return 返回图标res id
     */
    int getIcon();

    /**
     * 执行获取媒体操作
     *
     * @return
     */
    void start(Activity activity);


}
