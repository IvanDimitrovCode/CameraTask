package com.example.ivandimitrov.cameratask;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ivan Dimitrov on 1/18/2017.
 */

public class CustomFileListAdapter extends ArrayAdapter<File> {

    private final Activity context;
    private ArrayList<File> fileList = new ArrayList<File>();

    public CustomFileListAdapter(Activity context, ArrayList<File> fileList) {
        super(context, 0, fileList);
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.file_list_element, null, true);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.file_element_name);
            viewHolder.image = (ImageView) view.findViewById(R.id.file_element_image);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.txtTitle.setText(fileList.get(position).getName());
        viewHolder.image.setImageBitmap(BitmapFactory.decodeFile(fileList.get(position).getAbsolutePath()));
        return view;
    }

    static class ViewHolder {
        TextView  txtTitle;
        ImageView image;
    }
}