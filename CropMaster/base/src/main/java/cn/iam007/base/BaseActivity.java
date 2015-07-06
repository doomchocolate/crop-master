package cn.iam007.base;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.List;

import cn.iam007.base.utils.LogUtil;
import cn.iam007.base.utils.PlatformUtils;

public abstract class BaseActivity extends AppCompatActivity {
    protected final static String TAG = "BaseActivity";

    /**
     * 是否是debug模式，如果不是，所有debug开头的函数不会执行
     */
    protected static boolean DEBUG_MODE = true;

    static {
        DEBUG_MODE = false;
    }

    private FrameLayout mContainer = null;
    private Toolbar mToolbar;

    private SystemBarTintManager mTintManager = null;
    private static String mLauncherClass = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.iam007_activity_base);

        // 获取启动的activity
        if (mLauncherClass == null) {
            PackageManager packageManager = this.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
            ComponentName name = intent.getComponent();
            mLauncherClass = name.getClassName();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.primary);
        //        mTintManager.setNavigationBarTintEnabled(true);

        mContainer = (FrameLayout) findViewById(R.id.container);

        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (isLaunchActivity()){
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            PlatformUtils.applyFonts(this, mToolbar);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 获取activity的toolbar实例
     *
     * @return
     */
    public Toolbar getToolbar() {
        return mToolbar;
    }

    /**
     * 设置状态栏的背景颜色
     *
     * @param color
     */
    public final void setStatusBarTintColor(int color) {
        mTintManager.setStatusBarTintColor(color);
    }

    /**
     * 设置工具栏的背景颜色
     *
     * @param color
     */
    public final void setToolbarBackgroundColor(int color) {
        mToolbar.setBackgroundColor(color);
    }

    /**
     * 设置导航栏背景颜色
     *
     * @param color
     */
    public final void setNavigationBarTintColor(int color) {
        mTintManager.setNavigationBarTintColor(color);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView(int layoutResID) {
        View.inflate(this, layoutResID, mContainer);
        PlatformUtils.applyFonts(this, mContainer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        boolean launch = needLaunchMainActivity();
        if (launch) {
            try {
                Intent intent = new Intent();
                Class launcherClass = ClassLoader.getSystemClassLoader().loadClass(mLauncherClass);
                intent.setClass(this, launcherClass);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
        super.finish();
    }

    /**
     * 是否是启动的activity
     */
    private boolean isLaunchActivity() {
        return this.getClass().getName().equalsIgnoreCase(mLauncherClass);
    }

    private boolean needLaunchMainActivity() {
        boolean launcher = false;
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1000);

            LogUtil.d(TAG, "================running app===============");
            String packageName;
            String className = null;
            ActivityManager.RunningTaskInfo _info = null;
            for (ActivityManager.RunningTaskInfo info : list) {
                packageName = info.topActivity.getPackageName();
                className = info.topActivity.getClassName();
                if (packageName.equalsIgnoreCase(getPackageName())) {
                    _info = info;
                    break;
                }
            }

            if (_info.numActivities == 1) {
                if (!className.equalsIgnoreCase(mLauncherClass)) {
                    launcher = true;
                }

            }
        } catch (Exception e) {

        }

        return launcher;
    }
}
