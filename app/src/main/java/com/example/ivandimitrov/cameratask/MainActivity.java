package com.example.ivandimitrov.cameratask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements CustomAdapter.PictureTypeListener, CustomAdapter.FlashListener {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private String   chosenType    = CustomAdapter.PICTURE_TYPE_JPG;
    private String[] mPlanetTitles = {"Settings", "Other"};
    private Context               context;
    private Button                mCameraButton;
    private Button                mDisplayButton;
    private DrawerLayout          mDrawerLayout;
    private ListView              mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private Camera            mCamera;
    private CameraPreview     mPreview;
    private Camera.Parameters mParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        initButtons();
        initDrawerMenu();
        mCamera = getCameraInstance();

        mParams = mCamera.getParameters();
        mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParams);

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    //===============================================
    private void initDrawerMenu() {
        ArrayList<DrawerOption> list = new ArrayList<>();
        list.add(new DrawerOption(DrawerOption.DRAWER_WITH_CHECKBOX));
        list.add(new DrawerOption(DrawerOption.DRAWER_WITH_RADIO));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new CustomAdapter(this, list, this, this));

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                null,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Title");
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Title");
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    //=======================================================
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    void showSavePictureConfirmation() {
        new AlertDialog.Builder(context).setTitle("Picture taken")
                .setMessage("Do you want to save the picture")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCamera.takePicture(null, null, mPicture);
                        Log.d("CAMERA", "START PREVIEW");
                        mCamera.startPreview();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCamera.startPreview();
                    }
                }).show();
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d("ERROR", "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Log.d("CAMERA", "SAVE PICTURE");
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("ERROR", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("ERROR", "Error accessing file: " + e.getMessage());
            }
        }
    };

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + chosenType);
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private void initButtons() {
        mCameraButton = (Button) findViewById(R.id.button_capture);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.stopPreview();
                showSavePictureConfirmation();
            }
        });

        mDisplayButton = (Button) findViewById(R.id.button_display);
        mDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FileListActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
        });
    }

    @Override
    public void onPictureTypeChange(String newType) {
        chosenType = newType;
    }

    @Override
    public void onFlashToggle() {
        if (mParams.getFlashMode() == Camera.Parameters.FLASH_MODE_OFF) {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            mCamera.setParameters(mParams);
            Toast.makeText(this, "Flash ON", Toast.LENGTH_LONG).show();
        } else {
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParams);
            Toast.makeText(this, "Flash OFF", Toast.LENGTH_LONG).show();
        }
    }
}