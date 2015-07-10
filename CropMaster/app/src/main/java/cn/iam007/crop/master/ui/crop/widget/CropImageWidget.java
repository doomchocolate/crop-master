package cn.iam007.crop.master.ui.crop.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.iam007.base.utils.PlatformUtils;
import cn.iam007.base.utils.ViewUtils;
import cn.iam007.crop.master.R;

/**
 * Created by Administrator on 2015/7/3.
 */
public class CropImageWidget extends RelativeLayout {

    // 需要裁剪的图片
    private ZoomImageView mImageView;

    // 用于表示裁剪区域
    private View mPreviewArea;

    // 用于表示是那种裁剪方式
    private TYPE mType = TYPE.FOUR;


    public enum TYPE {
        NONE(0), SECOND(2), THIRD(3), FOUR(4), SIX(6);

        int value = 0;

        private TYPE(int value) {
            this.value = value;
        }

        public int toInt() {
            return value;
        }

        // 手写的从int到enum的转换函数
        public static TYPE valueOf(int value) {
            switch (value) {
                case 0:
                    return NONE;
                case 2:
                    return SECOND;
                case 3:
                    return THIRD;
                case 4:
                    return FOUR;
                case 6:
                    return SIX;
                default:
                    return null;
            }
        }
    }

    public CropImageWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    private int mOriginalWidth = 0;
    private int mOriginalHeight = 0;

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

        initPreviewGridArea();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
    }

    /**
     * 设置需要裁剪的图片Uri地址
     *
     * @param uri 需要裁剪的图片Uri地址
     */
    public void setCropImage(Uri uri) {
        mImageView.setImageURI(uri);
    }

    private ImageView mPreviewGrid_1_1;
    private ImageView mPreviewGrid_1_2;
    private ImageView mPreviewGrid_1_3;
    private ImageView mPreviewGrid_2_1;
    private ImageView mPreviewGrid_2_2;
    private ImageView mPreviewGrid_2_3;


    private void initPreviewGridArea() {
        mPreviewArea = View.inflate(getContext(), R.layout.activity_crop_preview_grid, null);
        addView(mPreviewArea);

        mPreviewGrid_1_1 = (ImageView) mPreviewArea.findViewById(R.id.index_1_1);
        mPreviewGrid_1_2 = (ImageView) mPreviewArea.findViewById(R.id.index_1_2);
        mPreviewGrid_1_3 = (ImageView) mPreviewArea.findViewById(R.id.index_1_3);
        mPreviewGrid_2_1 = (ImageView) mPreviewArea.findViewById(R.id.index_2_1);
        mPreviewGrid_2_2 = (ImageView) mPreviewArea.findViewById(R.id.index_2_2);
        mPreviewGrid_2_3 = (ImageView) mPreviewArea.findViewById(R.id.index_2_3);
    }

    public void setPreviewGridSize(int x, int y, int width) {
        LayoutParams layoutParams = (LayoutParams) mPreviewArea.getLayoutParams();
        layoutParams.leftMargin = x;
        layoutParams.topMargin = y;
        layoutParams.width = width;
        int gap =
                getResources().getDimensionPixelSize(R.dimen.crop_activity_preview_grid_gap_width);
        layoutParams.height = (int) ((width - 2 * gap) / 3.0f * 2) + gap;
        mPreviewArea.setLayoutParams(layoutParams);
    }

    /**
     * 设置裁剪的类型
     * 参考（@link cn.iam007.crop.master.ui.crop.widget.CropImageWidget）
     *
     * @param type 裁剪的类型
     */
    public void setType(TYPE type, boolean force) {
        if ((type != TYPE.NONE) && ((mType != type) || force)) {
            mType = type;

            mPreviewGrid_1_1.setImageResource(R.drawable.crop_activity_preview_grid_1);
            mPreviewGrid_1_2.setImageResource(R.drawable.crop_activity_preview_grid_2);

            mPreviewGrid_1_3.setVisibility(View.INVISIBLE);
            mPreviewGrid_2_1.setVisibility(View.INVISIBLE);
            mPreviewGrid_2_2.setVisibility(View.INVISIBLE);
            mPreviewGrid_2_3.setVisibility(View.INVISIBLE);

            Point point = ViewUtils.translateLocationWithOther(mPreviewGrid_1_1, this);
            int x = point.x;
            int y = point.y;
            int width = 0;
            int height = 0;

            View boundView = null;

            switch (type) {
                case SECOND:
                    boundView = mPreviewGrid_1_2;
                    break;

                case THIRD:
                    mPreviewGrid_1_3.setImageResource(R.drawable.crop_activity_preview_grid_3);
                    mPreviewGrid_1_3.setVisibility(View.VISIBLE);
                    boundView = mPreviewGrid_1_3;
                    break;

                case FOUR:
                    mPreviewGrid_2_1.setImageResource(R.drawable.crop_activity_preview_grid_3);
                    mPreviewGrid_2_2.setImageResource(R.drawable.crop_activity_preview_grid_4);
                    mPreviewGrid_2_1.setVisibility(View.VISIBLE);
                    mPreviewGrid_2_2.setVisibility(View.VISIBLE);
                    boundView = mPreviewGrid_2_2;
                    break;

                case SIX:
                    mPreviewGrid_1_3.setImageResource(R.drawable.crop_activity_preview_grid_3);
                    mPreviewGrid_2_1.setImageResource(R.drawable.crop_activity_preview_grid_4);
                    mPreviewGrid_2_2.setImageResource(R.drawable.crop_activity_preview_grid_5);
                    mPreviewGrid_2_3.setImageResource(R.drawable.crop_activity_preview_grid_6);
                    mPreviewGrid_1_3.setVisibility(View.VISIBLE);
                    mPreviewGrid_2_1.setVisibility(View.VISIBLE);
                    mPreviewGrid_2_2.setVisibility(View.VISIBLE);
                    mPreviewGrid_2_3.setVisibility(View.VISIBLE);
                    boundView = mPreviewGrid_2_3;
                    break;
            }

            point = ViewUtils.translateLocationWithOther(boundView, this);
            width = point.x + boundView.getWidth() - x;
            height = point.y + boundView.getHeight() - y;

            mImageView.setMinZoom(((float) width) / mOriginalWidth);
            mImageView.setRestrictArea(x, y, width, height, true);
        }
    }

    public void debugCrop() {
        mImageView.debugCrop();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        setType(mType, true);
    }

    public static class CropImageInfo implements Parcelable {
        public Rect restrictArea;
        public Uri uri;
        public TYPE type;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(uri, PARCELABLE_WRITE_RETURN_VALUE);
            dest.writeInt(restrictArea.left);
            dest.writeInt(restrictArea.top);
            dest.writeInt(restrictArea.right);
            dest.writeInt(restrictArea.bottom);
            dest.writeInt(type.toInt());
        }

        public static final Parcelable.Creator<CropImageInfo> CREATOR =
                new Parcelable.Creator<CropImageInfo>() {
                    public CropImageInfo createFromParcel(Parcel in) {
                        return new CropImageInfo(in);
                    }

                    public CropImageInfo[] newArray(int size) {
                        return new CropImageInfo[size];
                    }
                };

        private CropImageInfo(Parcel in) {
            uri = in.readParcelable(null);
            restrictArea = new Rect();
            restrictArea.left = in.readInt();
            restrictArea.top = in.readInt();
            restrictArea.right = in.readInt();
            restrictArea.bottom = in.readInt();
            type = TYPE.valueOf(in.readInt());
        }

        public CropImageInfo() {
        }

        public CropImageInfo(CropImageInfo info){
            restrictArea = info.restrictArea;
            uri = info.uri;
            type = info.type;
        }
    }

    public CropImageInfo getCropImageInfo() {
        CropImageInfo info = new CropImageInfo();

        info.restrictArea = mImageView.getRealRestrictArea();
        info.type = mType;
        info.uri = mImageView.getImageURI();

        return info;
    }
}
