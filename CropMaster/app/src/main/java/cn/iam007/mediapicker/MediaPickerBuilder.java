package cn.iam007.mediapicker;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import cn.iam007.base.utils.DialogBuilder;
import cn.iam007.base.utils.PlatformUtils;
import cn.iam007.crop.master.R;

/**
 * Created by Administrator on 2015/6/30.
 */
public class MediaPickerBuilder {
    private Activity mActivity;

    private MediaPickerBuilder(Activity activity) {
        mActivity = activity;
        mAdapter = new MediaPickerAdapter();
        mCallback = new MediaPickerListCallback();

        mAdapter.addSource(new MediaPickerGallery());
        mAdapter.addSource(new MediaPickerCamera());
    }

    public static MediaPickerBuilder newInstance(Activity activity) {
        return new MediaPickerBuilder(activity);
    }

    private MediaPickerAdapter mAdapter;
    private MediaPickerListCallback mCallback;

    public void showDialog() {
        DialogBuilder builder = new DialogBuilder(mActivity);
        builder.title("Pick a picture");
        builder.negativeText(R.string.cancel);
        builder.adapter(mAdapter, mCallback);
        builder.show();
    }

    private class MediaPickerListCallback implements MaterialDialog.ListCallback {

        @Override
        public void onSelection(MaterialDialog materialDialog, View view, int i,
                                CharSequence charSequence) {
            mAdapter.getItem(i).start(mActivity);
            materialDialog.dismiss();
        }
    }

    private class MediaPickerAdapter extends BaseAdapter {

        private ArrayList<MediaPickerSource> mSources = new ArrayList<>();

        public void addSource(MediaPickerSource source) {
            mSources.add(source);
        }


        @Override
        public int getCount() {
            return mSources.size();
        }

        @Override
        public MediaPickerSource getItem(int position) {
            return mSources.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(mActivity, R.layout.media_picker_dialog_item, null);
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(getItem(position).getName());
            PlatformUtils.applyFonts(mActivity, view);
            return view;
        }
    }
}
