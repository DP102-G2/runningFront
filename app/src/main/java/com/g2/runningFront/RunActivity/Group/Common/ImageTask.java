package com.g2.runningFront.RunActivity.Group.Common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.g2.runningFront.R;
import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.g2.runningFront.Common.Common.round;

public class ImageTask extends AsyncTask<Object, Integer, Bitmap> {

    private final static String TAG = "TAG_ImageTask";
    private String url;
    private int no, imageSize;
    private WeakReference<ImageView> imageViewWeakReference;

    public ImageTask(String url, int no, int imageSize, ImageView imageView) {
        this.url = url;
        this.no = no;
        this.imageSize = imageSize;
        this.imageViewWeakReference = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... objects) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("action", "getImage");
        jsonObject.addProperty("user_no", no);
        jsonObject.addProperty("user_no", no);
        jsonObject.addProperty("follow_no", no);
        jsonObject.addProperty("imageSize", imageSize);
        return getRemoteImage(url, jsonObject.toString());
    }

    private Bitmap getRemoteImage(String url, String jsonOut) {

        HttpURLConnection connection ;
        Bitmap bitmap = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(jsonOut);

            Log.e(TAG, "Output: " + jsonOut);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                bitmap = BitmapFactory.decodeStream(new BufferedInputStream(connection.getInputStream()));
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        ImageView imageView = imageViewWeakReference.get();
        if (isCancelled() || imageView == null) {
            return;
        }
        if (bitmap != null) {
            imageView.setImageBitmap(round(bitmap));
        } else {
            bitmap = BitmapFactory.decodeResource(imageView.getResources(), R.drawable.user_no_image);
            imageView.setImageBitmap(round(bitmap));
        }

    }
}
