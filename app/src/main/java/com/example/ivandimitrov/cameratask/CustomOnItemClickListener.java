package com.example.ivandimitrov.cameratask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ivan Dimitrov on 1/23/2017.
 */

public class CustomOnItemClickListener implements AdapterView.OnItemClickListener {
    private ArrayList<File> mFileList;
    private Activity        mActivity;
    private ListView        mListView;

    public CustomOnItemClickListener(ArrayList<File> fileList, Activity activity, ListView listView) {
        mFileList = fileList;
        mActivity = activity;
        mListView = listView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Uri path = FileProvider.getUriForFile(mActivity,
                "com.example.ivandimitrov.cameratask.provider", mFileList.get(i));
        Intent intent = new Intent(Intent.ACTION_VIEW);//ACTION_GET_CONTENT
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(path, "image/*");
        mActivity.startActivity(intent);
        animateClick(i);

//        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }

    public void animateClick(int index) {
        final int position = index;
        final Animation translateUpAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.anim_translate_up);
        final Animation translateDownAnimation = AnimationUtils.loadAnimation(mActivity, R.anim.anim_translate_down);

        for (int i = 0; i < mFileList.size(); i++) {
            if (i == position) {
                continue;
            }
            View view = getViewByPosition(i, mListView);
            if (i > position) {
                view.startAnimation(translateDownAnimation);
            } else {
                view.startAnimation(translateUpAnimation);
            }
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
