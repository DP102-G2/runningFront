package com.g2.runningFront.RunActivity;


import android.app.Activity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.Run.Achieve;
import com.g2.runningFront.ShopActivity.Adimage;
import com.g2.runningFront.ShopActivity.ShopMainFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


public class RunAchieveFragment extends Fragment {

    private Activity activity;
    private TextView tvachieve_value1,tvachieve_value2,tvachieve_value3,tvachieve_value4,
            tvachieve_value5,tvachieve_value6,tvachieve_value7,tvachieve_value8,tvachieve_value9;
    private CommonTask tvachieve_value1Task,tvachieve_value2Task,tvachieve_value3Task,tvachieve_value4Task,
                       tvachieve_value5Task,tvachieve_value6Task,tvachieve_value7Task,tvachieve_value8Task,tvachieve_value9Task;
    private static final String TAG = "TAG_SpotListFragment";
    Achieve achieve;
    int user_no  ;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        Common.signIn(activity);
        user_no=Common.getUserNo(activity);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle(R.string.achieve);
        return inflater.inflate(R.layout.fragment_run_achieve, container, false);


    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvachieve_value1 = view.findViewById(R.id.tvachieve_value1);
        tvachieve_value2 = view.findViewById(R.id.tvachieve_value2);
        tvachieve_value3 = view.findViewById(R.id.tvachieve_value3);
        tvachieve_value4 = view.findViewById(R.id.tvachieve_value4);
        tvachieve_value5 = view.findViewById(R.id.tvachieve_value5);
        tvachieve_value6 = view.findViewById(R.id.tvachieve_value6);
        tvachieve_value7 = view.findViewById(R.id.tvachieve_value7);
        tvachieve_value8 = view.findViewById(R.id.tvachieve_value8);
        tvachieve_value9 = view.findViewById(R.id.tvachieve_value9);




/*              1  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById1");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_value").getAsString();
                tvachieve_value1.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }

        ///////////////////////////////
        /*              2  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById2");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_value").getAsString();
                tvachieve_value2.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }

        ///////////////////////////////
        /*              3  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById3");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_value").getAsString();
                tvachieve_value3.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }


        ///////////////////////////////
        /*              4 */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById4");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_count").getAsString();
                tvachieve_value4.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }



        ///////////////////////////////
        /*              5  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById5");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_count").getAsString();
                tvachieve_value5.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }



        ///////////////////////////////
        /*              6  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById6");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_count").getAsString();
                tvachieve_value6.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }



        ///////////////////////////////
        /*              7  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById7");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_count").getAsString();
                tvachieve_value7.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }



        ///////////////////////////////
        /*              8  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById8");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_count").getAsString();
                tvachieve_value8.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }



        ///////////////////////////////
        /*              9  */
        if (Common.networkConnected(activity)) {
            String url = Common.URL_SERVER + "/AchieveServlet";
            String achieve_value;
            Achieve user= new Achieve(user_no);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action", "findById9");
            jsonObject.addProperty("user_no",user_no);
            List<Achieve> achieves = null;
            try {
                String jsonIn = new CommonTask(url , jsonObject.toString()).execute().get();
                JsonObject jObject = new Gson().fromJson(jsonIn,JsonObject.class);
                achieve_value = jObject.get("achieve_count").getAsString();
                tvachieve_value9.setText(String.valueOf(achieve_value));
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        } else {
            Common.showToast(activity, R.string.textNoNetwork);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tvachieve_value1Task != null) {
            tvachieve_value1Task.cancel(true);
            tvachieve_value1Task = null;
        }
        if (tvachieve_value2Task != null) {
            tvachieve_value2Task.cancel(true);
            tvachieve_value2Task = null;
        }
        if (tvachieve_value3Task != null) {
            tvachieve_value3Task.cancel(true);
            tvachieve_value3Task = null;
        }
        if (tvachieve_value4Task != null) {
            tvachieve_value4Task.cancel(true);
            tvachieve_value4Task = null;
        }
        if (tvachieve_value5Task != null) {
            tvachieve_value5Task.cancel(true);
            tvachieve_value5Task = null;
        }
        if (tvachieve_value6Task != null) {
            tvachieve_value6Task.cancel(true);
            tvachieve_value6Task = null;
        }
        if (tvachieve_value7Task != null) {
            tvachieve_value7Task.cancel(true);
            tvachieve_value7Task = null;
        }
        if (tvachieve_value8Task != null) {
            tvachieve_value8Task.cancel(true);
            tvachieve_value8Task = null;
        }
        if (tvachieve_value9Task != null) {
            tvachieve_value9Task.cancel(true);
            tvachieve_value9Task = null;
        }

    }


}