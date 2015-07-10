package cn.iam007.crop.master.ui.entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.iam007.base.BaseActivity;
import cn.iam007.base.utils.PlatformUtils;
import cn.iam007.base.utils.SharedPreferenceUtil;
import cn.iam007.crop.master.BuildConfig;
import cn.iam007.crop.master.R;

/**
 * Created by qq on 2015/7/7.
 */
public class WelcomeActivity extends BaseActivity implements OnClickListener, OnPageChangeListener {

    private ViewPager viewPager;
    private ViewPagerAdapter vpAdapter;
    private ArrayList<View> views;

    private static final int[] pics = {R.drawable.ic_launcher, R.drawable.ic_launcher,
            R.drawable.ic_launcher, R.drawable.ic_launcher};

    private ImageView[] points;

    private int currentIndex;

    private String firstRunKey;

    @Override
    protected boolean notDisplayToolbar() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstRunKey = "first.run." + PlatformUtils.getVersionCode(this);
        boolean firstRun = SharedPreferenceUtil.getBoolean(firstRunKey, true);

        if (BuildConfig.DEBUG || firstRun) {
            setContentView(R.layout.activity_welcome);
            initView();
            initData();
        } else {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        views = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpAdapter = new ViewPagerAdapter(views);
    }

    private void initData() {
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        viewPager.setAdapter(vpAdapter);
        viewPager.setOnPageChangeListener(this);
        initPoint();
    }

    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

        points = new ImageView[pics.length];

        for (int i = 0; i < pics.length; i++) {
            points[i] = (ImageView) linearLayout.getChildAt(i);
            points[i].setEnabled(true);
            points[i].setOnClickListener(this);
            points[i].setTag(i);
        }

        currentIndex = 0;
        points[currentIndex].setEnabled(false);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int position, float arg1, int arg2) {
        if (position == pics.length - 1) {
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            this.startActivity(intent);

            SharedPreferenceUtil.setBoolean(firstRunKey, false);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurDot(position);
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    private void setCurDot(int position) {
        if (position < 0 || position > pics.length - 1 || currentIndex == position) {
            return;
        }
        points[position].setEnabled(false);
        points[currentIndex].setEnabled(true);
        currentIndex = position;
    }
}
