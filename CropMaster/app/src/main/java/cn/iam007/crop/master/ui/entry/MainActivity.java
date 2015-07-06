package cn.iam007.crop.master.ui.entry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import cn.iam007.base.BaseActivity;
import cn.iam007.crop.master.R;
import cn.iam007.crop.master.ui.crop.CropActivity;
import cn.iam007.mediapicker.MediaPickerGallery;
import cn.iam007.mediapicker.MediaPickerSource;

/**
 * Created by Administrator on 2015/7/3.
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MediaPickerGallery().start(MainActivity.this);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == MediaPickerSource.GALLERY_IMAGE && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.setClass(this, CropActivity.class);
            intent.setData(result.getData());
            startActivity(intent);
        }
    }
}