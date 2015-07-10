package cn.iam007.crop.master.ui.decorate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.iam007.base.BaseActivity;
import cn.iam007.base.utils.DialogBuilder;
import cn.iam007.base.utils.ImageUtils;
import cn.iam007.base.utils.LogUtil;
import cn.iam007.base.utils.PlatformUtils;
import cn.iam007.base.utils.StringUtils;
import cn.iam007.base.utils.UriUtils;
import cn.iam007.crop.master.R;
import cn.iam007.crop.master.ui.crop.widget.CropImageWidget;

/**
 * Created by Administrator on 2015/7/8.
 */
public class DecorateActivity extends BaseActivity {

    public final static String KEY_TITLE = "title";
    public final static String KEY_NEXT = "next";
    public final static String KEY_CROP = "crop";
    public final static String KEY_INDEX = "index";
    public final static String KEY_INDEX_ICON = "index_icon";

    private Intent mNextIntent = null;
    private CropImageWidget.CropImageInfo info;

    private ImageView mIndexIcon = null;
    private ImageView mDecorateImage = null;

    private final static int DIRECTION_HORIZONTAL = 0;
    private final static int DIRECTION_VERTICAL = 1;
    private int mDirection = -1; // 0表示横向，1表示竖向
    private View mHorizontalBtn;
    private View mVerticalBtn;

    private int mPreviewLeftTopX;
    private int mPreviewLeftTopY;
    private int mPreviewWidth;
    private int mPreviewHeight;

    private int mIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String title = intent.getStringExtra(KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }

        mNextIntent = intent.getParcelableExtra(KEY_NEXT);
        setContentView(R.layout.activity_decorate);

        info = intent.getParcelableExtra(KEY_CROP);
        LogUtil.d("restrictArea:" + info.restrictArea);
        LogUtil.d("uri:" + info.uri);
        LogUtil.d("type:" + info.type);

        mIndex = intent.getIntExtra(KEY_INDEX, -1);

        mPreviewLeftTopX = info.restrictArea.left;
        mPreviewLeftTopY = info.restrictArea.top;
        mPreviewWidth = info.restrictArea.right - mPreviewLeftTopX + 1;
        mPreviewHeight = info.restrictArea.bottom - mPreviewLeftTopY + 1;

        View view = findViewById(R.id.decorate_container);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = PlatformUtils.getScreenWidth(this);
        layoutParams.height = PlatformUtils.getScreenWidth(this);
        view.setLayoutParams(layoutParams);

        int indexIconRes = intent.getIntExtra(KEY_INDEX_ICON, 0);
        mIndexIcon = (ImageView) findViewById(R.id.decorate_index_image);
        mIndexIcon.setImageResource(indexIconRes);

        mDecorateImage = (ImageView) findViewById(R.id.decorate_source_image);

        // 初始化横向纵向按钮
        mHorizontalBtn = findViewById(R.id.horizontal_style_btn);
        mHorizontalBtn.setOnClickListener(mDirectionClickListener);
        mVerticalBtn = findViewById(R.id.vertical_style_btn);
        mVerticalBtn.setOnClickListener(mDirectionClickListener);
        setDirection(DIRECTION_HORIZONTAL, false);

        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                generateTmpCropImage();
            }
        }).start();

        // 初始化seekbar
        mAdjustSeekbar = (SeekBar) findViewById(R.id.adjust_bound_seekbar);
        mAdjustSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float currentNormalize, targetScale;

                if (mDirection == DIRECTION_HORIZONTAL) {
                    currentNormalize = mHorizontalNormalize;
                    targetScale = mHorizontalTargetScale;
                } else {
                    currentNormalize = mVerticalNormalize;
                    targetScale = mVerticalTargetScale;
                }
                float currentScale = 1.0f + (targetScale - 1.0f) * progress / 100.f;

                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(mIndexIcon, "scaleX", currentNormalize,
                                currentScale),
                        ObjectAnimator.ofFloat(mIndexIcon, "scaleY", currentNormalize,
                                currentScale),
                        ObjectAnimator.ofFloat(mDecorateImage, "scaleX", currentNormalize,
                                currentScale),
                        ObjectAnimator.ofFloat(mDecorateImage, "scaleY", currentNormalize,
                                currentScale));
                set.start();

                if (mDirection == DIRECTION_HORIZONTAL) {
                    mHorizontalNormalize = currentScale;
                } else {
                    mVerticalNormalize = currentScale;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private SeekBar mAdjustSeekbar = null;

    private final static int SHOW_PROGRESS_DIALOG = 0x01;
    private final static int DISMISS_PROGRESS_DIALOG = 0x02;
    private final static int SHOW_DECORATE_IMAGE = 0x03;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_DECORATE_IMAGE:
                    String filePath;
                    if (mDirection == DIRECTION_HORIZONTAL) {
                        filePath = mHorizontalFile.getAbsolutePath();

                        ViewGroup.LayoutParams layoutParams = mDecorateImage.getLayoutParams();
                        layoutParams.height = PlatformUtils.getScreenWidth(DecorateActivity.this);
                        layoutParams.width =
                                (int) (mHorizontalWidth * (layoutParams.height / (float) mHorizontalHeight));
                        mDecorateImage.setLayoutParams(layoutParams);

                        mHorizontalTargetScale = layoutParams.height / (float) layoutParams.width;
                    } else {
                        filePath = mVerticalFile.getAbsolutePath();

                        ViewGroup.LayoutParams layoutParams = mDecorateImage.getLayoutParams();
                        layoutParams.width = PlatformUtils.getScreenWidth(DecorateActivity.this);
                        layoutParams.height =
                                (int) (mVerticalHeight * (layoutParams.width / (float) mVerticalWidth));
                        mDecorateImage.setLayoutParams(layoutParams);

                        mVerticalTargetScale = layoutParams.width / (float) layoutParams.height;
                    }

                    ImageUtils.showImageByFile(filePath, mDecorateImage);
                    break;
                case DISMISS_PROGRESS_DIALOG:
                    dismissProgressDialog();
                    break;
            }
            return true;
        }
    });

    private void setDirection(int direction) {
        setDirection(direction, true);
    }

    private void setDirection(int direction, boolean refreshDecorateImage) {
        if (direction != mDirection) {
            if (direction == DIRECTION_HORIZONTAL) {
                mVerticalBtn.setSelected(false);
                mHorizontalBtn.setSelected(true);
                mDirection = direction;
            } else if (direction == DIRECTION_VERTICAL) {
                mVerticalBtn.setSelected(true);
                mHorizontalBtn.setSelected(false);
                mDirection = direction;
            }

            if (refreshDecorateImage) {
                mHandler.sendEmptyMessage(SHOW_DECORATE_IMAGE);

                float currentScale, startScale, targetScale;
                if (mDirection == DIRECTION_HORIZONTAL) {
                    currentScale = mHorizontalNormalize;
                    startScale = mVerticalNormalize;
                    targetScale = mHorizontalTargetScale;
                } else {
                    currentScale = mVerticalNormalize;
                    startScale = mHorizontalNormalize;
                    targetScale = mVerticalTargetScale;
                }

                AnimatorSet set = new AnimatorSet();
                set.playTogether(
                        ObjectAnimator.ofFloat(mIndexIcon, "scaleX", startScale,
                                currentScale),
                        ObjectAnimator.ofFloat(mIndexIcon, "scaleY", startScale,
                                currentScale),
                        ObjectAnimator.ofFloat(mDecorateImage, "scaleX", startScale,
                                currentScale),
                        ObjectAnimator.ofFloat(mDecorateImage, "scaleY", startScale,
                                currentScale));
                set.start();

                int progress = (int) ((currentScale - 1.0f) / (targetScale - 1.0f) * 100);
                mAdjustSeekbar.setProgress(progress);
            }
        }
    }

    // 横向图片真实宽度
    private int mHorizontalWidth = 0;
    private int mHorizontalHeight = 0;
    private File mHorizontalFile = null;
    private float mHorizontalNormalize = 1.0f; // 横向图片view当前的的scal值
    private float mHorizontalTargetScale = 1.0f; // 横向图片最终可以缩放的大小

    private int mVerticalWidth = 0;
    private int mVerticalHeight = 0;
    private File mVerticalFile = null;
    private float mVerticalNormalize = 1.0f; // 横向图片view当前的的scal值
    private float mVerticalTargetScale = 1.0f; // 横向图片最终可以缩放的大小

    private void generateTmpCropImage() {
        // 获取真实图片尺寸
        String filePath = UriUtils.getImageAbsolutePath(this, info.uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap source = BitmapFactory.decodeFile(filePath, options);
        int realWidth = options.outWidth;
        int realHeight = options.outHeight;

        int leftGap = mPreviewLeftTopX;
        int rightGap = realWidth - (mPreviewWidth + mPreviewLeftTopX);

        // 生成横向图片
        int cropX;
        int cropY = mPreviewLeftTopY;
        int cropWidth;
        int cropHeight = mPreviewHeight;
        if (leftGap < rightGap) {
            cropX = 0;
            cropWidth = mPreviewWidth + 2 * leftGap;
        } else {
            cropX = mPreviewLeftTopX - rightGap;
            cropWidth = mPreviewWidth + 2 * rightGap;
        }

        Bitmap output = Bitmap.createBitmap(source, cropX, cropY, cropWidth, cropHeight);
        mHorizontalWidth = cropWidth;
        mHorizontalHeight = cropHeight;
        mHorizontalFile = new File(DecorateManager.getCacheDir(this),
                DecorateManager.getCurrentSession() + "_h_" + mIndex + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mHorizontalFile);
            output.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        output.recycle();

        // 生成纵向图片
        int topGap = mPreviewLeftTopY;
        int bottomGap = realHeight - (mPreviewHeight + mPreviewLeftTopY);

        cropX = mPreviewLeftTopX;
        cropWidth = mPreviewWidth;
        if (topGap < bottomGap) {
            cropY = 0;
            cropHeight = mPreviewHeight + 2 * topGap;
        } else {
            cropY = mPreviewLeftTopY - bottomGap;
            cropHeight = mPreviewHeight + 2 * bottomGap;
        }

        LogUtil.d("bitmap.width=" + source.getWidth());
        LogUtil.d("bitmap.height=" + source.getHeight());
        LogUtil.d("cropX=" + cropX);
        LogUtil.d("cropY=" + cropY);
        LogUtil.d("cropWidth=" + cropWidth);
        LogUtil.d("cropHeight=" + cropHeight);
        output = Bitmap.createBitmap(source, cropX, cropY, cropWidth, cropHeight);
        mVerticalWidth = cropWidth;
        mVerticalHeight = cropHeight;
        mVerticalFile = new File(DecorateManager.getCacheDir(this),
                DecorateManager.getCurrentSession() + "_v_" + mIndex + ".png");
        try {
            fos = new FileOutputStream(mVerticalFile);
            output.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        output.recycle();

        // 释放资源
        source.recycle();

        Message msg = mHandler.obtainMessage(SHOW_DECORATE_IMAGE);
        mHandler.sendMessage(msg);
        mHandler.sendEmptyMessage(DISMISS_PROGRESS_DIALOG);
    }

    private View.OnClickListener mDirectionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.horizontal_style_btn:
                    setDirection(DIRECTION_HORIZONTAL);
                    break;
                case R.id.vertical_style_btn:
                    setDirection(DIRECTION_VERTICAL);
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mNextIntent != null) {
            getMenuInflater().inflate(R.menu.activity_crop_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_crop_menu_finish, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_step:
                doNext();
                break;

            case R.id.action_finish:
                doFinish();
                break;
        }

        super.onOptionsItemSelected(item);
        return true;
    }

    private Rect _getCropRect() {
        int screenWidth = PlatformUtils.getScreenWidth(this);
        int viewWidth = 0, viewHeight = 0;
        float scale;
        if (mDirection == DIRECTION_HORIZONTAL) {
            viewWidth = (int) (mDecorateImage.getWidth() * mHorizontalNormalize);
            viewHeight = (int) (mDecorateImage.getHeight() * mHorizontalNormalize);
            scale = viewWidth / (float) mHorizontalWidth;
        } else {
            viewWidth = (int) (mDecorateImage.getWidth() * mVerticalNormalize);
            viewHeight = (int) (mDecorateImage.getHeight() * mVerticalNormalize);
            scale = viewHeight / (float) mVerticalHeight;
        }

        int left, top, right, bottom;
        if (mDirection == DIRECTION_HORIZONTAL) {
            left = (int) ((viewWidth - screenWidth) / 2 / scale);
            top = 0;
            right = Math.min((int) (screenWidth / ((float) viewHeight) * mPreviewHeight + left - 1),
                    mHorizontalWidth - left - 1);
            bottom = mHorizontalHeight - 1;
        } else {
            left = 0;
            top = (int) ((viewHeight - screenWidth) / 2 / scale);
            right = mVerticalWidth - 1;
            bottom = Math.min((int) (screenWidth / ((float) viewWidth) * mPreviewWidth + top - 1),
                    mVerticalHeight - top - 1);
        }

        Rect rect = new Rect(left, top, right, bottom);
        return rect;
    }

    private void _pushCropRecord() {
        String filePath;

        if (mDirection == DIRECTION_HORIZONTAL) {
            filePath = mHorizontalFile.getAbsolutePath();
        } else {
            filePath = mVerticalFile.getAbsolutePath();
        }

        Rect rect = _getCropRect();
        DecorateManager.pushCropRecord(this, filePath, rect);
    }

    private void doNext() {
        _pushCropRecord();
        startActivity(mNextIntent);
    }

    private void doFinish() {
        _pushCropRecord();

        Intent intent = new Intent();
        intent.setClass(this, PreviewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        DecorateManager.popCropRecord();
    }
}
