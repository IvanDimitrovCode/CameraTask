package com.example.ivandimitrov.cameratask;

/**
 * Created by Ivan Dimitrov on 1/17/2017.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DrawerOption> {
    public static final String PICTURE_TYPE_JPG = ".jpg";
    public static final String PICTURE_TYPE_PNG = ".png";

    private final Activity context;
    private ArrayList<DrawerOption> drawerOptionList = new ArrayList<DrawerOption>();
    private PictureTypeListener pictureTypeListener;
    private FlashListener       flashListener;

    public CustomAdapter(Activity context, ArrayList<DrawerOption> drawerOptionList, PictureTypeListener pictureTypeListener, FlashListener flashListener) {
        super(context, 0, drawerOptionList);
        this.context = context;
        this.drawerOptionList = drawerOptionList;
        this.pictureTypeListener = pictureTypeListener;
        this.flashListener = flashListener;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        int itemType;
        itemType = drawerOptionList.get(position).getType();

        switch (itemType) {
            case DrawerOption.DRAWER_WITH_CHECKBOX:
                view = checkBoxSetup(view);
                break;
            case DrawerOption.DRAWER_WITH_RADIO:
                view = radioButtonSetup(view);
                break;
            default:
                break;
        }
        return view;
    }

    private View checkBoxSetup(View view) {
        ViewHolderCheckbox viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            viewHolder = new ViewHolderCheckbox();
            view = inflater.inflate(R.layout.list_item_checkbox, null, false);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.button = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    flashListener.onFlashToggle();
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderCheckbox) view.getTag();
        }
        viewHolder.txtTitle.setText("Flash");
        return view;
    }

    private View radioButtonSetup(View view) {
        ViewHolderRadio viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            viewHolder = new ViewHolderRadio();
            view = inflater.inflate(R.layout.list_item_radio, null, false);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.JPGButton = (RadioButton) view.findViewById(R.id.radio1);
            viewHolder.PNGButton = (RadioButton) view.findViewById(R.id.radio2);

            viewHolder.JPGButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pictureTypeListener.onPictureTypeChange(PICTURE_TYPE_JPG);
                }
            });
            viewHolder.JPGButton.setChecked(true);
            viewHolder.PNGButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pictureTypeListener.onPictureTypeChange(PICTURE_TYPE_PNG);
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderRadio) view.getTag();
        }
        viewHolder.txtTitle.setText("Save type");
        return view;
    }

    static class ViewHolderRadio {
        TextView    txtTitle;
        RadioButton JPGButton;
        RadioButton PNGButton;
    }

    static class ViewHolderCheckbox {
        TextView txtTitle;
        CheckBox button;
    }

    interface PictureTypeListener {
        public void onPictureTypeChange(String newType);
    }

    interface FlashListener {
        public void onFlashToggle();
    }
}