package cn.iam007.crop.master.ui.decorate;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import java.util.ArrayList;

import cn.iam007.base.BaseActivity;
import cn.iam007.base.utils.ImageUtils;
import cn.iam007.crop.master.R;
import cn.iam007.crop.master.ui.crop.widget.CropImageWidget;

/**
 * Created by Administrator on 2015/7/9.
 */
public class PreviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);
        initPreviewArea();

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                DecorateManager.doDecorate(PreviewActivity.this, new DecorateManager.Callback() {
                    @Override
                    public void onFinish(String info) {
                        dismissProgressDialog();
                        DecorateManager.finishDecorate();
                        finish();
                    }
                });
            }
        });
    }

    private void initPreviewArea() {
        final View previewArea = findViewById(R.id.preview_area);
        final ImageView mPreviewGrid_1_1 = (ImageView) previewArea.findViewById(R.id.index_1_1);
        final ImageView mPreviewGrid_1_2 = (ImageView) previewArea.findViewById(R.id.index_1_2);
        final ImageView mPreviewGrid_1_3 = (ImageView) previewArea.findViewById(R.id.index_1_3);
        final ImageView mPreviewGrid_2_1 = (ImageView) previewArea.findViewById(R.id.index_2_1);
        final ImageView mPreviewGrid_2_2 = (ImageView) previewArea.findViewById(R.id.index_2_2);
        final ImageView mPreviewGrid_2_3 = (ImageView) previewArea.findViewById(R.id.index_2_3);

        previewArea.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int width = previewArea.getWidth();
                        int gap =
                                getResources().getDimensionPixelSize(
                                        R.dimen.crop_activity_preview_grid_gap_width);
                        int gridSize = (width - 2 * gap) / 3;

                        ViewGroup.LayoutParams layoutParams = mPreviewGrid_1_1.getLayoutParams();
                        layoutParams.width = gridSize;
                        layoutParams.height = gridSize;
                        mPreviewGrid_1_1.setLayoutParams(layoutParams);

                        layoutParams = mPreviewGrid_1_2.getLayoutParams();
                        layoutParams.width = gridSize;
                        layoutParams.height = gridSize;
                        mPreviewGrid_1_2.setLayoutParams(layoutParams);

                        layoutParams = mPreviewGrid_1_3.getLayoutParams();
                        layoutParams.width = gridSize;
                        layoutParams.height = gridSize;
                        mPreviewGrid_1_3.setLayoutParams(layoutParams);

                        layoutParams = mPreviewGrid_2_1.getLayoutParams();
                        layoutParams.width = gridSize;
                        layoutParams.height = gridSize;
                        mPreviewGrid_2_1.setLayoutParams(layoutParams);

                        layoutParams = mPreviewGrid_2_2.getLayoutParams();
                        layoutParams.width = gridSize;
                        layoutParams.height = gridSize;
                        mPreviewGrid_2_2.setLayoutParams(layoutParams);

                        layoutParams = mPreviewGrid_2_3.getLayoutParams();
                        layoutParams.width = gridSize;
                        layoutParams.height = gridSize;
                        mPreviewGrid_2_3.setLayoutParams(layoutParams);
                    }
                });

        CropImageWidget.TYPE type = DecorateManager.getCurrentType();
        ArrayList<String> files = DecorateManager.getPreviewCropFiles();

        mPreviewGrid_1_1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPreviewGrid_1_2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPreviewGrid_1_3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPreviewGrid_2_1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPreviewGrid_2_2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mPreviewGrid_2_3.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ImageUtils.showImageByFile(files.get(0), mPreviewGrid_1_1);
        ImageUtils.showImageByFile(files.get(1), mPreviewGrid_1_2);

        mPreviewGrid_1_3.setVisibility(View.INVISIBLE);
        mPreviewGrid_2_1.setVisibility(View.GONE);
        mPreviewGrid_2_2.setVisibility(View.GONE);
        mPreviewGrid_2_3.setVisibility(View.GONE);

        switch (type) {
            case SECOND:
                break;

            case THIRD:
                ImageUtils.showImageByFile(files.get(2), mPreviewGrid_1_3);
                mPreviewGrid_1_3.setVisibility(View.VISIBLE);
                break;

            case FOUR:
                ImageUtils.showImageByFile(files.get(2), mPreviewGrid_2_1);
                ImageUtils.showImageByFile(files.get(3), mPreviewGrid_2_2);
                mPreviewGrid_2_1.setVisibility(View.VISIBLE);
                mPreviewGrid_2_2.setVisibility(View.VISIBLE);
                break;

            case SIX:
                ImageUtils.showImageByFile(files.get(2), mPreviewGrid_1_3);
                ImageUtils.showImageByFile(files.get(3), mPreviewGrid_2_1);
                ImageUtils.showImageByFile(files.get(4), mPreviewGrid_2_2);
                ImageUtils.showImageByFile(files.get(5), mPreviewGrid_2_3);
                mPreviewGrid_1_3.setVisibility(View.VISIBLE);
                mPreviewGrid_2_1.setVisibility(View.VISIBLE);
                mPreviewGrid_2_2.setVisibility(View.VISIBLE);
                mPreviewGrid_2_3.setVisibility(View.VISIBLE);
                break;
        }
    }
}
