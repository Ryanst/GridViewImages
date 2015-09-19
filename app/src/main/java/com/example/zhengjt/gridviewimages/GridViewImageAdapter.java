package com.example.zhengjt.gridviewimages;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by zhengjt on 15/9/18.
 */
public class GridViewImageAdapter extends ArrayAdapter<String> implements AbsListView.OnScrollListener {
    private Set<BitmapWorkerTask> taskCollection;
    private LruCache<String ,Bitmap> mMemoryCache;
    private GridView mPhotoWall;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private boolean isFirstEnter = true;

    public GridViewImageAdapter(Context context, int textViewResourceId, String[] objects, GridView photoWall){
        super(context,textViewResourceId,objects);
        mPhotoWall = photoWall;
        taskCollection = new HashSet<BitmapWorkerTask>();
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        mPhotoWall.setOnScrollListener(this);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String url = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_layout,null);
        }
        final ImageView photo = (ImageView) convertView.findViewById(R.id.photo);
        photo.setTag(url);
        setImageView(url, photo);
        return convertView;
    }
    private  void setImageView(String imageUrl,ImageView imageView){
        Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
        if(bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
        else{
            imageView.setImageResource(R.drawable.ic_launcher);
        }
    }
    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromMemoryCache(key) == null){
            mMemoryCache.put(key, bitmap);
        }
    }
    public Bitmap getBitmapFromMemoryCache(String key){
        return mMemoryCache.get(key);
    }

    /**
     * scroll状态改变的时候调用，状态有三种：停止，正在滚动，滚动后的滑行，这里检测停止状态然后载入图片
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            loadBitmap(mFirstVisibleItem,mVisibleItemCount);
        }
        else{
            cancelAllTasks();
        }
    }

    /**
     * onScroll，当list或grid停止scroll的时候会调用
     * 开始绘制的时候会调用多次的onScroll，这里设置isFirstEnter是为了第一次初始化的时候调用loadBitmap，之后的loadBitmap任务交给onScrollStateChanged
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        if(isFirstEnter && visibleItemCount > 0){
            loadBitmap(firstVisibleItem,visibleItemCount);
            isFirstEnter = false;
        }
    }

    private  void loadBitmap(int firstVisibleItem,int visibleItemCout){
        try{
            for(int i = firstVisibleItem;i < firstVisibleItem + visibleItemCout;i++){
                String imageUrl = Images.imageThumbUrls[i];
                Bitmap bitmap = getBitmapFromMemoryCache(imageUrl);
                if(bitmap == null){
                    BitmapWorkerTask task = new BitmapWorkerTask();
                    taskCollection.add(task);
                    task.execute(imageUrl);
                }
                else{
                    ImageView imageView = (ImageView)mPhotoWall.findViewWithTag(imageUrl);
                    if(imageView != null && bitmap != null){
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void cancelAllTasks(){
        if(taskCollection != null){
            for(BitmapWorkerTask task:taskCollection){
                task.cancel(false);
            }
        }
    }



    class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap> {
        private String imageUrl;

        @Override
        protected Bitmap doInBackground(String... params) {
            imageUrl = params[0];
            Bitmap bitmap = downloadBitmap(params[0]);
            if (bitmap != null) {
                addBitmapToMemoryCache(params[0], bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView imageView = (ImageView) mPhotoWall.findViewWithTag(imageUrl);
            if(imageView != null && bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
            taskCollection.remove(this);
        }

        private Bitmap downloadBitmap(String imageUrl) {
            Bitmap bitmap = null;
            HttpURLConnection con = null;
            try {
                URL url = new URL(imageUrl);
                con = (HttpsURLConnection) url.openConnection();
                con.setConnectTimeout(5000);
                con.setReadTimeout(10000);
                bitmap = BitmapFactory.decodeStream(con.getInputStream());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            finally {
                if(con != null)
                    con.disconnect();
            }
            return bitmap;
        }

    }
}
