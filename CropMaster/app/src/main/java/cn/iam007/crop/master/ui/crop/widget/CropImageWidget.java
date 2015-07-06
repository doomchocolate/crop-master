package cn.iam007.crop.master.ui.crop.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;

import cn.iam007.base.utils.LogUtil;
import cn.iam007.base.utils.PlatformUtils;
import cn.iam007.crop.master.R;

/**
 * Created by Administrator on 2015/7/3.
 */
public class CropImageWidget extends RelativeLayout {

    // 需要裁剪的图片
    private ZoomImageView mImageView;

    // 用于表示裁剪区域
    private LinearLayout mPreviewArea;

    // 用于表示是那种裁剪方式
    private TYPE mType;


    public enum TYPE {
        SECOND, THIRD, FOUR, SIX
    }

    public CropImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    /**
     * 设置裁剪的类型
     * 参考（@link cn.iam007.crop.master.ui.crop.widget.CropImageWidget）
     *
     * @param type 裁剪的类型
     */
    public void setType(TYPE type) {
        mType = type;
    }

    private int mOriginalWidth = 0;
    private int mOriginalHeight = 0;
    private float mScaleValue = 1.0f;

    private void init() {
        mImageView = new ZoomImageView(getContext());
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mOriginalWidth = PlatformUtils.getScreenWidth(getContext());
        mOriginalHeight = (int) (mOriginalWidth * 0.618);
        LayoutParams layoutParams =
                new LayoutParams(mOriginalWidth, mOriginalHeight);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mImageView, layoutParams);

        View view = new View(getContext());
        view.setBackgroundColor(Color.argb(0x66, 0, 0, 0));
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(view, layoutParams);

        mPreviewArea = new LinearLayout(getContext());
        mPreviewArea.setBackgroundColor(Color.argb(0x22, 0xFF, 0xFF, 0xFF));
        layoutParams = new LayoutParams(400, 200);
        layoutParams.leftMargin = 150;
        layoutParams.topMargin = 150;
        addView(mPreviewArea, layoutParams);

        mImageView.setMinZoom(400.f / mOriginalWidth);
        mImageView.setRestrictArea(150, 150, 400, 200);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        LogUtil.d("Width:" + width + ", Height:" + height);
    }

    /**
     * 设置需要裁剪的图片Uri地址
     *
     * @param uri 需要裁剪的图片Uri地址
     */
    public void setCropImage(Uri uri) {
        mImageView.setImageURI(uri);
//        ContentResolver contentResolver = getContext().getContentResolver();
//        try {
//            Bitmap photo = MediaStore.Images.Media.getBitmap(contentResolver, uri);
//            mOriginalWidth = PlatformUtils.getScreenWidth(getContext());
//            float scale = mOriginalWidth / photo.getWidth();
//            mOriginalHeight = (int) (photo.getHeight() * scale);
//            LayoutParams layoutParams = (LayoutParams) mImageView.getLayoutParams();
//            layoutParams.width = mOriginalWidth;
//            layoutParams.height = mOriginalHeight;
//            mImageView.setLayoutParams(layoutParams);
//            Bitmap bitmap = zoomBitmap(photo, scale);
//            mImageView.setImageBitmap(bitmap);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private Bitmap zoomBitmap(Bitmap bitmap, float scale) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newBmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);

        return newBmp;
    }

    private void doScale(float scale) {
        mScaleValue *= scale;
        ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
        layoutParams.width = (int) (mOriginalWidth * mScaleValue);
        layoutParams.height = (int) (mOriginalHeight * mScaleValue);
        mImageView.setLayoutParams(layoutParams);
        LogUtil.d("2Width:" + layoutParams.width + ", Height:" + layoutParams.height);
    }
}
