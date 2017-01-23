package com.example.ivandimitrov.cameratask;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

public class MainActivity extends AppCompatActivity implements CustomAdapter.PictureTypeListener,
        CustomAdapter.FlashListener, CustomAdapter.ZoomPercentageListener, CustomAdapter.ExposureListener {

    public static final  int MEDIA_TYPE_IMAGE       = 1;
    public static final  int MEDIA_TYPE_VIDEO       = 2;
    private static final int MY_PERMISSIONS_REQUEST = 1;

    private String mChosenType = CustomAdapter.PICTURE_TYPE_JPG;
    private ActionBarDrawerToggle mDrawerToggle;

    private static Camera            mCamera;
    private        Camera.Parameters mParams;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            camera.startPreview();

            if (pictureFile == null) {
                Log.d("ERROR", "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                Log.d("CAMERA", pictureFile.getAbsolutePath());
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d("ERROR", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("ERROR", "Error accessing file: " + e.getMessage());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (checkForPermission()) {
            runActivity();
        } else {
            askPermission();
        }
    }

    @Override
    protected void onDestroy() {
        if (mCamera != null) {
            mCamera.release();
        }
        super.onDestroy();
    }

    //===============================================
    private void initDrawerMenu() {
        ArrayList<DrawerOption> list = new ArrayList<>();
        list.add(new DrawerOption(DrawerOption.DRAWER_WITH_CHECKBOX));
        list.add(new DrawerOption(DrawerOption.DRAWER_WITH_RADIO));
        list.add(new DrawerOption(DrawerOption.DRAWER_WITH_SEEKBAR));
        list.add(new DrawerOption(DrawerOption.DRAWER_WITH_SEEKBAR_EX));

        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new CustomAdapter(this, list, this, this, this, this));

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
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
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

    private void runActivity() {
        initButtons();
        initDrawerMenu();
        mCamera = getCameraInstance();

        mParams = mCamera.getParameters();
        mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParams);

        CameraPreview preview1 = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(preview1);
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST);
    }

    private boolean checkForPermission() {
        int permissionCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int permissionWriteExternal = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCamera == PackageManager.PERMISSION_GRANTED &&
                permissionWriteExternal == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionGranted = false;
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                }
                break;
            }
        }
        if (permissionGranted) {
            runActivity();
        }
    }


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
        new AlertDialog.Builder(this).setTitle("Picture taken")
                .setMessage("Do you want to save the picture")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCamera.stopPreview();
                        mCamera.startPreview();
                        mCamera.takePicture(null, null, mPicture);
                        Log.d("CAMERA", "START PREVIEW");
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mCamera.startPreview();
                    }
                }).show();
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("MyCameraApp", "failed to create directory");
            return null;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + mChosenType);
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private void initButtons() {
        final Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        Button mCameraButton = (Button) findViewById(R.id.button_capture);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.stopPreview();
                view.startAnimation(scaleAnimation);
                showSavePictureConfirmation();
            }
        });

        final Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        Button mDisplayButton = (Button) findViewById(R.id.button_display);
        mDisplayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(rotateAnimation);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
//    }

    @Override
    public void onPictureTypeChange(String newType) {
        mChosenType = newType;
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

    @Override
    public void onZoomPercentageChange(int percentage) {
        mParams.setZoom(percentage);
        mCamera.setParameters(mParams);
    }

    @Override
    public void onExposurePercentageChange(int percentage) {
        int maxExposure = mParams.getMaxExposureCompensation();
        int minExposure = mParams.getMinExposureCompensation();
        int exposureRange;

        if (minExposure == 0) {
            exposureRange = minExposure + maxExposure;
        } else {
            exposureRange = (minExposure * (-1)) + maxExposure;
        }
        double valuePlus = exposureRange * ((double) percentage / 100);
        mParams.setExposureCompensation((int) (minExposure + valuePlus));
    }
}