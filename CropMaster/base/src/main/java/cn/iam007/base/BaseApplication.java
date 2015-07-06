package cn.iam007.base;

import android.app.Application;

import cn.iam007.base.utils.ImageUtils;

/**
 * Created by Administrator on 2015/7/3.
 */
public class BaseApplication extends Application {

    private static BaseApplication mApplication = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        // 初始化显示图片配置，初始化image loader
        ImageUtils.init(this);
    }

    public static BaseApplication getApplication() {
        return mApplication;
    }
}
