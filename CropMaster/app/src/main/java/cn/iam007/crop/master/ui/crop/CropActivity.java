package cn.iam007.crop.master.ui.crop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.security.KeyRep;

import cn.iam007.base.BaseActivity;
import cn.iam007.base.utils.PlatformUtils;
import cn.iam007.crop.master.R;
import cn.iam007.crop.master.ui.crop.widget.CropImageWidget;

/**
 * Created by Administrator on 2015/7/3.
 */
public class CropActivity extends BaseActivity {
    private CropImageWidget mCropImageWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop);
        mCropImageWidget = (CropImageWidget) findViewById(R.id.crop_widget);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        mCropImageWidget.setCropImage(uri);

        final int width = PlatformUtils.getScreenWidth(this);
        int height = (int) (width * 0.618);
        ViewGroup.LayoutParams layoutParams = mCropImageWidget.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mCropImageWidget.setLayoutParams(layoutParams);

        final View previewInfoContainer = findViewById(R.id.preview_info_container);
        previewInfoContainer.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int[] location = new int[2];
                        previewInfoContainer.getLocationInWindow(location);
                        int x = location[0];
                        int y = 30;
                        int tWidth = width - x - 30;
                        mCropImageWidget.setPreviewGridSize(x, 30, tWidth);

                        previewInfoContainer.getViewTreeObserver().removeGlobalOnLayoutListener(
                                this);

                        View view = findViewById(R.id.preview_text);
                        RelativeLayout.LayoutParams layoutParams1 =
                                (RelativeLayout.LayoutParams) view.getLayoutParams();
                        layoutParams1.leftMargin = x;
                        view.setLayoutParams(layoutParams1);
                    }
                });

        initStyleBtn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_step:
                Toast.makeText(this, "click on next step", Toast.LENGTH_SHORT).show();
                mCropImageWidget.debugCrop();
                break;
        }

        super.onOptionsItemSelected(item);
        return true;
    }

    private View mPreStyleBtn = null;

    private void initStyleBtn() {
        findViewById(R.id.style_btn_2).setOnClickListener(mStyleBtnClickListener);
        findViewById(R.id.style_btn_3).setOnClickListener(mStyleBtnClickListener);
        findViewById(R.id.style_btn_4).setOnClickListener(mStyleBtnClickListener);
        findViewById(R.id.style_btn_6).setOnClickListener(mStyleBtnClickListener);

        mPreStyleBtn = findViewById(R.id.style_btn_4);
        mPreStyleBtn.setSelected(true);
    }

    private CropImageWidget.TYPE mType = CropImageWidget.TYPE.FOUR;

    private View.OnClickListener mStyleBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CropImageWidget.TYPE type = CropImageWidget.TYPE.NONE;
            mPreStyleBtn.setSelected(false);
            switch (v.getId()) {
                case R.id.style_btn_2:
                    type = CropImageWidget.TYPE.SECOND;
                    mPreStyleBtn = findViewById(R.id.style_btn_2);
                    break;

                case R.id.style_btn_3:
                    type = CropImageWidget.TYPE.THIRD;
                    mPreStyleBtn = findViewById(R.id.style_btn_3);
                    break;

                case R.id.style_btn_4:
                    type = CropImageWidget.TYPE.FOUR;
                    mPreStyleBtn = findViewById(R.id.style_btn_4);
                    break;

                case R.id.style_btn_6:
                    type = CropImageWidget.TYPE.SIX;
                    mPreStyleBtn = findViewById(R.id.style_btn_6);
                    break;
            }

            if (type != CropImageWidget.TYPE.NONE) {
                mCropImageWidget.setType(type, false);
                mType = type;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mPreStyleBtn.setSelected(true);
                }
            });
        }
    };
}
