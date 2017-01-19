package com.example.ivandimitrov.cameratask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Ivan Dimitrov on 1/18/2017.
 */

public class FileListActivity extends AppCompatActivity {
    private ListView              mListView;
    private CustomFileListAdapter mAdapter;
    private ArrayList<File>       mFileList;
    private Context               context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_list_activity);
        context = this;
        mFileList = new ArrayList<>();
        File[] allFiles;
        File folder = new java.io.File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        allFiles = folder.listFiles();
        for (File file : allFiles) {
            mFileList.add(file);
        }
        mAdapter = new CustomFileListAdapter(this, mFileList);
        mListView = (ListView) this.findViewById(R.id.file_list);
        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("PATH", mFileList.get(i).getPath());
                Uri path = FileProvider.getUriForFile(context,
                        "com.example.ivandimitrov.cameratask.provider", mFileList.get(i));

                Intent intent = new Intent(Intent.ACTION_VIEW);//ACTION_GET_CONTENT
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(path, "image/*");
                startActivity(intent);
            }
        });
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle().equals("Rename")) {

        } else if (item.getTitle().equals("Delete")) {
            mFileList.remove(mFileList.get(info.position));
        } else {
            return false;
        }
        mAdapter.notifyDataSetChanged();
        return true;
    }
}

