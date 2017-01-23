package com.example.ivandimitrov.cameratask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ivan Dimitrov on 1/18/2017.
 */

public class FileListActivity extends AppCompatActivity {
    private CustomFileListAdapter mAdapter;
    private ArrayList<File>       mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list_activity);
        mFileList = new ArrayList<>();
        File folder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File[] allFiles = folder.listFiles();

        if (!(allFiles.length < 1)) {
            for (File file : allFiles) {
                mFileList.add(file);
            }
        }

        mAdapter = new CustomFileListAdapter(this, mFileList);
        final ListView listView = (ListView) this.findViewById(R.id.file_list);
        listView.setAdapter(mAdapter);
        registerForContextMenu(listView);
//        final Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_button);
        listView.setOnItemClickListener(new CustomOnItemClickListener(mFileList, this, listView));

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.file_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Log.d("ITEMS", mFileList.get(info.position).getName());
            String[] menuItems = {"Rename", "Delete"};
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    AdapterView.AdapterContextMenuInfo mItemSelectedInfo;

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        mItemSelectedInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle().equals("Rename")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setTitle(R.string.rename);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newName = input.getText().toString();
                    if (newName.matches("^.*[^a-zA-Z0-9 ].*$")) {
                        return;
                    }
                    for (File file : mFileList) {
                        if (file.getName().equals(newName)) {
                            return;
                        }
                    }
                    String fileDirectory = mFileList.get(mItemSelectedInfo.position).getAbsolutePath();

                    //TAKING THE EXTENSION OF THE FILE
                    String fileExtension = fileDirectory.substring(fileDirectory.lastIndexOf("."));

                    //TAKING THE DIRECTORY OF THE FILE
                    fileDirectory = fileDirectory.substring(0, fileDirectory.lastIndexOf("/"));

                    File fileRenamed = new File(fileDirectory, newName + fileExtension);
                    boolean success = mFileList.get(mItemSelectedInfo.position).renameTo(fileRenamed);
                    mFileList.set(mItemSelectedInfo.position, fileRenamed);
                    mAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else if (item.getTitle().equals("Delete")) {
            final Animation fadeAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_fade);
            fadeAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    File fileForDelete = mFileList.get(mItemSelectedInfo.position);
                    mFileList.remove(mFileList.get(mItemSelectedInfo.position));
                    fileForDelete.delete();
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.delete_message);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mItemSelectedInfo.targetView.startAnimation(fadeAnimation);
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        } else {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
    }
}

