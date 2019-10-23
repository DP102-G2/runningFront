package com.g2.runningFront.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.g2.runningFront.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdimageaTask extends AsyncTask<Object, Integer, Bitmap> {
    private final static String TAG = "AllimageTask";
    private String url,outStr;
    private int  imageSize;
    private String pro_no;
    private int ad_no;

    /* ImageTask的屬性strong參照到SpotListFragment內的imageView不好，
        會導致SpotListFragment進入背景時imageView被參照而無法被釋放，
        而且imageView會參照到Context，也會導致Activity無法被回收。
        改採weak參照就不會阻止imageView被回收 */
    private WeakReference<ImageView> imageViewWeakReference;

    // 取單張圖片
    public AdimageaTask(String url, int ad_no, int imageSize, ImageView ivadimage) {
        this.url= url;
        this.ad_no= ad_no;
        this.imageSize= imageSize;
        this.imageViewWeakReference = new WeakReference<>(ivadimage);

    }

    // 取完圖片後使用傳入的ImageView顯示，適用於顯示多張圖片
    public AdimageaTask(String url, String pro_no, int imageSize, ImageView imageView) {
        this.url = url;
        this.pro_no = pro_no;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }



    public AdimageaTask(String url, String outStr, ImageView imageView) {
        this.url = url;
        this.outStr=outStr;
        this.imageViewWeakReference = new WeakReference<>(imageView);

    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getadImage");
        jsonObject.addProperty("pro_no", pro_no);
        jsonObject.addProperty("imageSize", imageSize);
        return getRemoteImage(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.pro_image);
        }
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {
        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true); // allow inputs
            connection.setDoOutput(true); // allow outputs
            connection.setUseCaches(false); // do not use a cached copy
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);
            Log.d(TAG, "output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(
                        new BufferedInputStream(connection.getInputStream()));
            } else {
                Log.d(TAG, "response code: " + responseCode);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return bitmap;
    }
}