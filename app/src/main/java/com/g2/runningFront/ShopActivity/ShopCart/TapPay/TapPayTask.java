package com.g2.runningFront.ShopActivity.ShopCart.TapPay;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TapPayTask extends AsyncTask<String, Integer, String> {

    private final static String TAG = "TAG_TapTask";
    private String url, outStr, apiKey;

    public TapPayTask(String url, String outStr, String apiKey) {
        this.url = url;
        this.outStr = outStr;
        this.apiKey = apiKey;
    }

    @Override
    protected String doInBackground(String... strings) {
        return getRemoteDate();
    }

    private String getRemoteDate() {
        HttpURLConnection connection = null;
        StringBuilder inStr = new StringBuilder();

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setChunkedStreamingMode(0);
            connection.setUseCaches(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("charset", "UTF-8");

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-api-key", apiKey);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bw.write(outStr);
            Log.d(TAG, "output: " + outStr);
            bw.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;

                if ((line = br.readLine()) != null) {
                    inStr.append(line);
                }


            } else {
                Log.d(TAG, "responseCode: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return inStr.toString();
    }
}
