package cn.iam007.crop.master.ui.crop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

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
        mCropImageWidget = (CropImageWidget) findViewById(R.id.cropWidget);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        mCropImageWidget.setCropImage(uri);

        int width = PlatformUtils.getScreenWidth(this);
        int height = (int) (width * 0.618);
        ViewGroup.LayoutParams layoutParams = mCropImageWidget.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mCropImageWidget.setLayoutParams(layoutParams);
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
                break;
        }
        return true;
    }
}
