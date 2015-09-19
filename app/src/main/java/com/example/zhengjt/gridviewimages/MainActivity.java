package com.example.zhengjt.gridviewimages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {
    private GridView mPhotoWall;
    private GridViewImageAdapter adapter;
    private GridViewVolleyImageAdapter volleyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhotoWall = (GridView) findViewById(R.id.photo_wall);
        volleyAdapter = new GridViewVolleyImageAdapter(this, Images.imageThumbUrls);
        //adapter = new GridViewImageAdapter(this,0,Images.imageThumbUrls,mPhotoWall);
        mPhotoWall.setAdapter(volleyAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cancelAllTasks();
    }
}
