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
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<DrawerOption> {
    public static final String PICTURE_TYPE_JPG = ".jpg";
    public static final String PICTURE_TYPE_PNG = ".png";

    private final Activity mContext;
    private ArrayList<DrawerOption> mDrawerOptionList = new ArrayList<DrawerOption>();
    private PictureTypeListener    mPictureTypeListener;
    private FlashListener          mFlashListener;
    private ZoomPercentageListener mZoomListener;
    private ExposureListener       mExposureListener;
    private int                    mItemType;


    public CustomAdapter(Activity context, ArrayList<DrawerOption> drawerOptionList,
                         PictureTypeListener pictureTypeListener, FlashListener flashListener,
                         ZoomPercentageListener zoomListener, ExposureListener exposureListener) {
        super(context, 0, drawerOptionList);
        this.mContext = context;
        this.mDrawerOptionList = drawerOptionList;
        this.mPictureTypeListener = pictureTypeListener;
        this.mFlashListener = flashListener;
        this.mZoomListener = zoomListener;
        this.mExposureListener = exposureListener;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        mItemType = mDrawerOptionList.get(position).getType();

        switch (mItemType) {
            case DrawerOption.DRAWER_WITH_CHECKBOX:
                view = checkBoxSetup(view);
                break;
            case DrawerOption.DRAWER_WITH_RADIO:
                view = radioButtonSetup(view);
                break;
            case DrawerOption.DRAWER_WITH_SEEKBAR:
                view = seekBarSetup(view);
                break;
            case DrawerOption.DRAWER_WITH_SEEKBAR_EX:
                view = seekBarSetup(view);
                break;
            default:
                break;
        }
        return view;
    }

    private View seekBarSetup(View view) {
        ViewHolderSeekbar viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            viewHolder = new ViewHolderSeekbar();
            view = inflater.inflate(R.layout.list_item_seekbar, null, false);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.seekBar = (SeekBar) view.findViewById(R.id.checkbox);
            viewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    if (mItemType == DrawerOption.DRAWER_WITH_SEEKBAR) {
                        mZoomListener.onZoomPercentageChange(progress);
                    } else if (mItemType == DrawerOption.DRAWER_WITH_SEEKBAR_EX) {
                        mExposureListener.onExposurePercentageChange(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderSeekbar) view.getTag();
        }
        if (mItemType == DrawerOption.DRAWER_WITH_SEEKBAR) {
            viewHolder.txtTitle.setText(R.string.zoom);
        } else if (mItemType == DrawerOption.DRAWER_WITH_SEEKBAR_EX) {
            viewHolder.txtTitle.setText(R.string.exposure);
        }
        return view;
    }

    private View checkBoxSetup(View view) {
        ViewHolderCheckbox viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            viewHolder = new ViewHolderCheckbox();
            view = inflater.inflate(R.layout.list_item_checkbox, null, false);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.button = (CheckBox) view.findViewById(R.id.checkbox);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFlashListener.onFlashToggle();
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderCheckbox) view.getTag();
        }
        viewHolder.txtTitle.setText(R.string.flash);
        return view;
    }

    private View radioButtonSetup(View view) {
        ViewHolderRadio viewHolder = null;
        if (view == null) {
            LayoutInflater inflater = mContext.getLayoutInflater();
            viewHolder = new ViewHolderRadio();
            view = inflater.inflate(R.layout.list_item_radio, null, false);
            viewHolder.txtTitle = (TextView) view.findViewById(R.id.title);
            viewHolder.JPGButton = (RadioButton) view.findViewById(R.id.radio1);
            viewHolder.PNGButton = (RadioButton) view.findViewById(R.id.radio2);

            viewHolder.JPGButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPictureTypeListener.onPictureTypeChange(PICTURE_TYPE_JPG);
                }
            });
            viewHolder.JPGButton.setChecked(true);
            viewHolder.PNGButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPictureTypeListener.onPictureTypeChange(PICTURE_TYPE_PNG);
                }
            });
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderRadio) view.getTag();
        }
        viewHolder.txtTitle.setText(R.string.image_type);
        return view;
    }

    static class ViewHolderSeekbar {
        TextView txtTitle;
        SeekBar  seekBar;
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

    interface ExposureListener {
        void onExposurePercentageChange(int percentage);
    }

    interface ZoomPercentageListener {
        void onZoomPercentageChange(int percentage);
    }

    interface PictureTypeListener {
        void onPictureTypeChange(String newType);
    }

    interface FlashListener {
        void onFlashToggle();
    }

    interface CameraPropsListener extends FlashListener, PictureTypeListener, ZoomPercentageListener, ExposureListener {

    }
}