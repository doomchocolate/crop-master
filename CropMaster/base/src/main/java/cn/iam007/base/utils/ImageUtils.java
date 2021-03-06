package cn.iam007.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.iam007.base.R;


public class ImageUtils {
    private final static String CACHE_DIR_IMAGE = "image";

    /**
     * 根据后缀名判断是否是图片文件
     *
     * @param filename
     * @return 是否是图片结果true or false
     */
    public static boolean isImage(String filename) {
        String type = getFileType(filename);
        if (type != null
                && (type.equals("jpg") || type.equals("gif")
                || type.equals("png") || type.equals("jpeg")
                || type.equals("bmp") || type.equals("wbmp")
                || type.equals("ico") || type.equals("jpe"))) {
            return true;
        }
        return false;
    }

    /**
     * 获取文件后缀名
     *
     * @param fileName
     * @return 文件后缀名
     */
    public static String getFileType(String fileName) {
        if (fileName != null) {
            int typeIndex = fileName.lastIndexOf(".");
            if (typeIndex != -1) {
                String fileType = fileName.substring(typeIndex + 1)
                        .toLowerCase();
                return fileType;
            }
        }
        return "";
    }

    /**
     * 获取应用外部图片缓存地址
     *
     * @param context
     * @return
     */
    public static File getExtCacheDirImage(Context context) {
        File file = StorageUtils.getCacheDirectory(context);

        return new File(file, CACHE_DIR_IMAGE);
    }

    /**
     * 获取应用内部私有图片缓存地址
     *
     * @param context
     * @return
     */
    public static File getInternalCacheDirImage(Context context) {
        File file = StorageUtils.getCacheDirectory(context, false);

        return new File(file, CACHE_DIR_IMAGE);
    }

    public static void init(Context context) {
        // 设置图片的缓存的位置
        File cacheDir = getExtCacheDirImage(context);
        File reserveCacheDir = getInternalCacheDirImage(context);
        UnlimitedDiscCache discCache = new UnlimitedDiscCache(cacheDir,
                reserveCacheDir, new Md5FileNameGenerator());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY + 1)
                .threadPoolSize(5).denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache()).diskCache(discCache)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    public static DisplayImageOptions getOptionsFadeIn() {
        return getOptionsFadeIn(250);
    }

    public static DisplayImageOptions getOptionsFadeIn(int milliseconds) {
        return new DisplayImageOptions.Builder().cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(true)
                .displayer(new FadeInBitmapDisplayer(milliseconds))
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.iam007_image_show_on_loading_default)
                .build();
    }

    public static DisplayImageOptions getOptionsRound(int size) {
        return new DisplayImageOptions.Builder().cacheOnDisk(true)
                .cacheInMemory(true)
                .displayer(new RoundedBitmapDisplayer((int) (size)))
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
    }

    public static DisplayImageOptions getOptionsFadeInWithoutCache(int milliseconds) {
        return new DisplayImageOptions.Builder().cacheOnDisk(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(false)
                .displayer(new FadeInBitmapDisplayer(milliseconds))
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.iam007_image_show_on_loading_default)
                .build();
    }

    //    public static DisplayImageOptions getOptionCircle() {
    //        return new DisplayImageOptions.Builder().cacheOnDisk(true)
    //                .cacheInMemory(true)
    //                .displayer(new CircleBitmapDisplayer())
    //                .build();
    //    }

    /**
     * 显示网络图片
     *
     * @param imageUrl  网络图片地址
     * @param imageView 显示图片的对象
     * @param options   显示图片的选项
     */
    public static void showImageByUrl(
            String imageUrl, ImageView imageView, DisplayImageOptions options) {
        ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
    }

    /**
     * 显示网络图片
     *
     * @param imageUrl  网络图片地址
     * @param imageView 显示图片的对象
     */
    public static void showImageByUrl(String imageUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUrl,
                imageView,
                getOptionsFadeIn());
    }

    /**
     * 显示sdcard上图片
     *
     * @param filePath  图片的绝对路径
     * @param imageView 显示图片的对象
     */
    public static void showImageByFile(String filePath, ImageView imageView) {
        ImageLoader.getInstance().displayImage("file://" + filePath,
                imageView,
                getOptionsFadeInWithoutCache(250));
    }

    /**
     * 截取图片文件的一部分
     *
     * @param filePath
     * @param cropRect
     * @param outputFilePath
     */
    public static boolean cropImageFile(String filePath, Rect cropRect, String outputFilePath) {
        boolean result = false;
        if (filePath != null && cropRect != null) {
            Bitmap source = BitmapFactory.decodeFile(filePath);

            int cropX = cropRect.left;
            int cropY = cropRect.top;
            int cropWidth = cropRect.right - cropRect.left + 1;
            int cropHeight = cropRect.bottom - cropRect.top + 1;
            Bitmap output = Bitmap.createBitmap(source, cropX, cropY, cropWidth, cropHeight);

            File outputFile = new File(outputFilePath);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                output.compress(Bitmap.CompressFormat.PNG, 90, fos);
                result = true;
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
            source.recycle();
        }
        return result;
    }
}
