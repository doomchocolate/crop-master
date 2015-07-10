package cn.iam007.crop.master.ui.decorate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.iam007.base.utils.FileUtils;
import cn.iam007.base.utils.ImageUtils;
import cn.iam007.base.utils.StringUtils;
import cn.iam007.crop.master.R;
import cn.iam007.crop.master.ui.crop.widget.CropImageWidget;

/**
 * Created by Administrator on 2015/7/8.
 */
public class DecorateManager {

    private static String currentSession = null;
    private static CropImageWidget.TYPE currentType;

    public static File getCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), ".tmp.crop");
    }

    public static void clearCache(Context context) {
        File file = new File(context.getExternalCacheDir(), ".tmp.crop");
        FileUtils.deleteFile(file);
        file.mkdirs();
    }

    public static void startDecorate(Context context, CropImageWidget.CropImageInfo info) {
        clearCache(context);

        currentSession = StringUtils.randomString(32);
        currentType = info.type;

        String[] titles = context.getResources().getStringArray(R.array.decorate_activity_title);
        int[] icons = {
                R.drawable.crop_activity_preview_grid_1,
                R.drawable.crop_activity_preview_grid_2,
                R.drawable.crop_activity_preview_grid_3,
                R.drawable.crop_activity_preview_grid_4,
                R.drawable.crop_activity_preview_grid_5,
                R.drawable.crop_activity_preview_grid_6};

        // 生成每一个区域的尺寸
        Rect[] rects = new Rect[6];
        int gap = context.getResources().getDimensionPixelSize(
                R.dimen.crop_activity_preview_grid_gap_width);
        int totalWidth = info.restrictArea.right - info.restrictArea.left + 1;
        int totalHeight = info.restrictArea.bottom - info.restrictArea.top + 1;
        int leftTopX = info.restrictArea.left;
        int leftTopY = info.restrictArea.top;
        int row = 0, col = 0;
        switch (info.type) {
            case SECOND:
                row = 1;
                col = 2;
                break;

            case THIRD:
                row = 1;
                col = 3;
                break;

            case FOUR:
                row = 2;
                col = 2;
                break;

            case SIX:
                row = 2;
                col = 3;
                break;

            default:
                return;
        }
        int gridWidth = (totalWidth - (col - 1) * gap) / col;
        for (int rowIndex = 0; rowIndex < row; rowIndex++) {
            for (int colIndex = 0; colIndex < col; colIndex++) {
                Rect rect = new Rect();
                rect.left = leftTopX + colIndex * (gap + gridWidth);
                rect.top = leftTopY + rowIndex * (gap + gridWidth);
                rect.right = rect.left + gridWidth - 1;
                rect.bottom = rect.top + gridWidth - 1;

                rects[rowIndex * col + colIndex] = rect;
            }
        }


        int step = info.type.toInt();
        int index = step - 1;

        Intent nextIntent = new Intent();
        nextIntent.setClass(context, DecorateActivity.class);
        CropImageWidget.CropImageInfo tInfo = new CropImageWidget.CropImageInfo(info);
        tInfo.restrictArea = rects[index];
        nextIntent.putExtra(DecorateActivity.KEY_CROP, tInfo);
        nextIntent.putExtra(DecorateActivity.KEY_TITLE, titles[index]);
        nextIntent.putExtra(DecorateActivity.KEY_INDEX_ICON, icons[index]);
        nextIntent.putExtra(DecorateActivity.KEY_INDEX, index);

        index--;
        while (index >= 0) {
            Intent intent = new Intent();
            intent.setClass(context, DecorateActivity.class);
            tInfo = new CropImageWidget.CropImageInfo(info);
            tInfo.restrictArea = rects[index];
            intent.putExtra(DecorateActivity.KEY_CROP, tInfo);
            intent.putExtra(DecorateActivity.KEY_TITLE, titles[index]);
            intent.putExtra(DecorateActivity.KEY_INDEX_ICON, icons[index]);
            intent.putExtra(DecorateActivity.KEY_INDEX, index);
            intent.putExtra(DecorateActivity.KEY_NEXT, nextIntent);

            nextIntent = intent;
            index--;
        }

        context.startActivity(nextIntent);

        // 初始化编辑session
        cropFiles = new ArrayList<>();
        cropRects = new ArrayList<>();
        cropActivities = new ArrayList<>();
    }

    public static String getCurrentSession() {
        return currentSession;
    }


    private static ArrayList<Activity> cropActivities;
    private static ArrayList<String> cropFiles;
    private static ArrayList<Rect> cropRects;

    /**
     * 用于保存修改状态
     *
     * @param file 图片文件
     * @param rect 需要截取图片文件的区域
     */
    public static void pushCropRecord(Activity activity, String file, Rect rect) {
        if (cropFiles != null && cropRects != null && cropActivities != null) {
            if (file != null && rect != null && activity != null) {
                cropFiles.add(file);
                cropRects.add(rect);
                cropActivities.add(activity);
            }
        }
    }

    public static void popCropRecord() {
        if (cropFiles != null && cropRects != null && cropActivities != null) {
            if (cropActivities.size() > 0) {
                cropActivities.remove(cropActivities.size() - 1);
            }

            if (cropFiles.size() > 0) {
                cropFiles.remove(cropFiles.size() - 1);
            }

            if (cropRects.size() > 0) {
                cropRects.remove(cropRects.size() - 1);
            }
        }
    }

    public interface Callback {
        void onFinish(String info);
    }

    public static void doDecorate(final Context context, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doDecorateInternal(context, callback);
            }
        }).start();
    }

    private static void doDecorateInternal(Context context, Callback callback) {
        // 创建文件夹
        File targetFolder =
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        context.getResources().getString(R.string.app_name));
        targetFolder.mkdirs();

        if (!targetFolder.isDirectory()) {
            // todo: 无法创建目录
            if (callback != null) {
                callback.onFinish("无法创建目录");
            }
            return;
        }

        String filePath;
        Rect rect;
        File outputFile;
        SimpleDateFormat formatter = new SimpleDateFormat(
                context.getResources().getString(R.string.save_file_prefix_format));
        String prefix = formatter.format(new Date(System.currentTimeMillis()));
        for (int index = cropActivities.size() - 1; index >= 0; index--) {
            filePath = cropFiles.get(index);
            rect = cropRects.get(index);

            outputFile = new File(targetFolder, prefix + "_" + index + ".png");
            if (ImageUtils.cropImageFile(filePath, rect, outputFile.getAbsolutePath())) {
                MediaScannerConnection.scanFile(context, new String[]{outputFile.getAbsolutePath()},
                        null, null);
            }
        }

        if (callback != null) {
            callback.onFinish("");
        }
    }

    public static void finishDecorate() {
        currentSession = null;
        cropFiles = null;
        cropRects = null;

        for (Activity activity : cropActivities) {
            try {
                activity.finish();
            } catch (Exception e) {

            }
        }
    }

    public static ArrayList<String> getPreviewCropFiles() {
        return cropFiles;
    }

    public static CropImageWidget.TYPE getCurrentType() {
        return currentType;
    }
}
