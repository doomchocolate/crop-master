package cn.iam007.crop.master.ui.entry;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import cn.iam007.base.BaseActivity;
import cn.iam007.base.Iam007Service;
import cn.iam007.base.utils.LogUtil;
import cn.iam007.crop.master.R;
import cn.iam007.crop.master.ui.crop.CropActivity;
import cn.iam007.mediapicker.MediaPickerBuilder;
import cn.iam007.mediapicker.MediaPickerCamera;
import cn.iam007.mediapicker.MediaPickerSource;

/**
 * Created by Administrator on 2015/7/3.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.main_select_image_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPickerBuilder builder = MediaPickerBuilder.newInstance(MainActivity.this);
                builder.showDialog();
            }
        });

        // 启动检查更新
        Intent intent = new Intent();
        intent.setClass(this, Iam007Service.class);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mExitHintToast = null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mExitHintToast != null) {
            mExitHintToast.cancel();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MediaPickerSource.GALLERY_IMAGE:
                    Intent intent = new Intent();
                    intent.setClass(this, CropActivity.class);
                    intent.setData(result.getData());
                    startActivity(intent);
                    break;

                case MediaPickerSource.CAPTURE_IMAGE:
                    intent = new Intent();
                    intent.setClass(this, CropActivity.class);
                    intent.setData(MediaPickerCamera.getUri());
                    startActivity(intent);
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭service
        Intent intent = new Intent();
        intent.setClass(this, Iam007Service.class);
        stopService(intent);
    }

    // 上次按下返回键的时间
    private long mPreBackPressedTS = 0;
    private Toast mExitHintToast = null;

    private Handler mToastHandler = new Handler();

    @Override
    public void onBackPressed() {
        LogUtil.d("onBackPressed!");
        long currentTS = System.currentTimeMillis();
        if (currentTS - mPreBackPressedTS < 3000) {
            super.onBackPressed();
        }

        if (mExitHintToast != null) {
            mExitHintToast.cancel();
        }
        mExitHintToast = Toast.makeText(this, R.string.exit_hint, Toast.LENGTH_SHORT);
        mExitHintToast.show();
        mPreBackPressedTS = currentTS;

        mToastHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mExitHintToast != null) {
                    mExitHintToast.cancel();
                    mExitHintToast = null;
                }
            }
        }, 3000);
    }
}
