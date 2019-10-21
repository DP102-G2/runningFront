package com.g2.runningFront.SettingActivity;


import android.app.Activity;
import android.graphics.Matrix;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

/* 有關 Layout */
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

/* 不知道為什麼要 import Res 目錄 */
import com.g2.runningFront.R;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.Common.CommonTask;
import com.g2.runningFront.Common.ImageTask;

/* 有關建立日期、日期格式 */
import com.google.gson.GsonBuilder;
import java.util.Date;

/* 使用 Gson */
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/* 有關照片儲存 */
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.ByteArrayOutputStream;

/* 有關照片壓縮 */
import android.util.Base64;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static android.app.Activity.RESULT_OK;
import static com.g2.runningFront.Common.Common.round;


public class SettingUpadteFragment extends Fragment {
    private static String TAG = "TAG_SettingUpdate";
    private Activity activity;
    private ImageView imageView;
    private EditText etId, etPW, etName, etEmail;

    private RadioGroup radioGroup;
    private RadioButton rbPublic, rbPrivate;

    private Gson gson;

    /* 請求使用照相程式的代碼 */
    private Uri contentUri;
    private byte[] image;
    private static final int REQ_TAKE_PIC = 30;
    private static final int REQ_PICK_PIC = 31;
    private static final int REQ_CROP_PIC = 32;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();

        /* 為了在此使用 Gson */
        gson = new Gson();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity.setTitle("修改會員資料");
        return inflater.inflate(R.layout.fragment_setting_upadte, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.imageView);
        etId = view.findViewById(R.id.etId);
        etPW = view.findViewById(R.id.etPW);
        etName = view.findViewById(R.id.etName);
        etEmail = view.findViewById(R.id.etEmail);
        /* 隱私單選按鈕群組 */
        radioGroup = view.findViewById(R.id.radioGroup);

        /* 列印出該會員資料 */
        final Bundle bundle = getArguments();

        if (bundle == null || bundle.getInt("user_no") == 0) {
            Log.e(TAG, "讀入的 user_no 不被許可");
            return;// 沒通過 bundle 檢驗,就跳出
        } else {
            int no = bundle.getInt("user_no");
            Log.d(TAG, "讀入的 user_no：" + no);

            if (Common.networkConnected(activity)) {

                /* 取得會員資料（純文字） */
                String url = Common.URL_SERVER + "SettingServlet";

                JsonObject jo = new JsonObject();
                jo.addProperty("action", "showUserInfo");
                jo.addProperty("user_no", no);
                String outStr = jo.toString();

                CommonTask loginTask = new CommonTask(url, outStr);

                try {

                    String strIn = loginTask.execute().get();
                    jo = gson.fromJson(strIn, JsonObject.class);

                    if(jo == null){
                        Common.toastShow(activity,"找不到會員資料");
                    } else {

                        Log.e(TAG, "傳回的 JsonObject：\n" + jo);

                        /* 印出會員資料 */
                        etId.setText(jo.get("user_id").getAsString());
                        etPW.setText(jo.get("user_pw").getAsString());
                        etName.setText(jo.get("user_name").getAsString());
                        etEmail.setText(jo.get("user_email").getAsString());
                        /* 根據資料控制隱私按鈕是目前是勾選哪一項 */
                        int private_code = jo.get("user_private").getAsInt();
                        switch(private_code){
                            case 0:
                                RadioButton rbPublic = view.findViewById(R.id.rbPublic);
                                rbPublic.setChecked(true);
                                break;
                            case 1:
                                RadioButton rbPrivate = view.findViewById(R.id.rbPrivate);
                                rbPrivate.setChecked(true);
                                break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                /* 取得會員大頭貼圖像 */
                JsonObject jo_image = new JsonObject();
                jo_image.addProperty("action","getImage");
                jo_image.addProperty("user_no", no);

                String url_image = Common.URL_SERVER + "SettingServlet";
                CommonTask imageTask = new CommonTask(url_image, jo_image.toString());

                Bitmap bitmap = null;

                try {
                    /* 用 Base64 解碼 Servlet 端 編碼而成的文字變成 byte[]
                     * 再用 BitmapFactory 把 byte[] 換成 bitmap 以供 UI 元件貼圖 */
                    byte[] image = Base64.decode(imageTask.execute().get(), Base64.DEFAULT);
                    bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                if (bitmap != null) {
                    imageView.setImageBitmap(round(bitmap));
                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_no_image);
                    imageView.setImageBitmap(round(bitmap));
                }

            }
        }

        /* 按下大頭貼，跳出選擇圖片來源選單 */
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(activity, view);
                popup.inflate(R.menu.setting_update_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            /* 拍攝照片 */
                            case R.id.take_pic:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                // 指定存檔路徑
                                File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                // 生成 File 物件，代表一段路徑
                                file = new File(file, "user_image.jpg");
                                /* 這路徑為上層路徑
                                 * 再帶入要新增的檔案名,作為完整路徑 */
                                contentUri = FileProvider.getUriForFile(
                                        activity, activity.getPackageName() + ".provider", file);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                                /* 在 intent 裡增加額外資料
                                 * 一開始建立 intent 是使用 MediaStore 的照片捕捉
                                 * 這邊是用 MediaStore 的向外輸出 */

                                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                                    startActivityForResult(intent, REQ_TAKE_PIC);
                                } else {
                                    Common.toastShow(activity,"照片儲存失敗");
                                }
                                return true;

                            /* 從相簿中選擇照片 */
                            case R.id.pick_pic:
                                Intent intent_pick = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent_pick, REQ_PICK_PIC);
                                return true;

                            /* 預設 */
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });

        /* 會員資料修改按鈕 */
        view.findViewById(R.id.btUpdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = etPW.getText().toString().trim();
                String name = etName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();


                if (Common.networkConnected(activity)) {

                    String url = Common.URL_SERVER + "SettingServlet";

                    JsonObject jo = new JsonObject();
                    jo.addProperty("action", "update");

                    int no = (int) bundle.getSerializable("user_no");
                    jo.addProperty("user_no", no);
                    jo.addProperty("user_pw", password);
                    jo.addProperty("user_name", name);
                    jo.addProperty("user_email", email);
                    /* 得知被按下的隱私按鈕是哪一個 */
                    int private_code = 0;
                    switch(radioGroup.getCheckedRadioButtonId()){
                        case R.id.rbPublic:
                            private_code = 0;
                            break;
                        case R.id.rbPrivate:
                            private_code = 1;
                            break;
                    }
                    jo.addProperty("user_private", private_code);

                    /* 編碼使用者大頭貼成 Base64 文字格式 */
                    if (image != null) {
                        jo.addProperty("user_imageBase64",
                                Base64.encodeToString(image, Base64.DEFAULT));//圖片轉檔成文字
                    }

                    String outStr = jo.toString();
                    CommonTask signUpTask = new CommonTask(url, outStr);

                    boolean isUpdate = false;
                    try {

                        String jsonIn = signUpTask.execute().get();
                        isUpdate = gson.fromJson(jsonIn, Boolean.class);
                        Log.d(TAG, "isUpdate = " + isUpdate);

                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    if (isUpdate) {
                        Log.d(TAG, "會員資料修改成功。");
                        Common.toastShow(activity, "會員資料已更新！");
                    } else{
                        Log.e(TAG, "會員資料修改失敗。");
                        Common.toastShow(activity, "會員資更新失敗");
                    }
                }else {
                    Common.toastShow(activity, "與伺服器連線失敗");
                }

            }
        });


        /* 註冊會員按鈕 */
//        view.findViewById(R.id.btSignUp).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                String id = etId.getText().toString().trim();
//                String password = etPW.getText().toString().trim();
//                String name = etName.getText().toString().trim();
//                String email = etEmail.getText().toString().trim();
//
//                if(id.length() <= 0 || password.length() <= 0
//                        || name.length() <= 0 || email.length() <= 0){
//                    Common.toastShow(activity,"輸入資料不符規定");
//                    return;
//                }
//
//                if (Common.networkConnected(activity)) {
//
//                    String url = Common.URL_SERVER + "SettingServlet";
//
//                    JsonObject jo = new JsonObject();
//                    jo.addProperty("action", "signup");
//                    jo.addProperty("user_id", id);
//                    jo.addProperty("user_pw", password);
//                    jo.addProperty("user_name", name);
//                    jo.addProperty("user_email", email);
//
//                    /* 會員註冊日期 */
//                    Gson gson = new GsonBuilder()
//                            .setDateFormat("yyyy-MM-dd")// 2020-01-02
//                            .create();
//                    /* 將 new Date() 轉為 Json，並且要符合以上的日期表示法
//                     * 但是在 Servlet 端會變成字串型態，需要再利用 Gson 轉成 Date */
//                    String date = gson.toJson(new Date());
//                    jo.addProperty("user_regtime", date);
//
//                    Log.d(TAG, "即將送出的註冊資料：\n" + jo);
//                    String outStr = jo.toString();
//                    CommonTask signUpTask = new CommonTask(url, outStr);
//
//                    boolean isSignUp = false;
//                    try {
//                        String jsonIn = signUpTask.execute().get();
//                        isSignUp = gson.fromJson(jsonIn, Boolean.class);
//                        Log.e(TAG, "isSignUp = " + isSignUp);
//
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                    if (isSignUp) {
//                        Log.e(TAG, "會員註冊成功。");
//                        Common.toastShow(activity, "會員註冊成功！");
//                        // ("註冊成功");
//                    } else{
//                        Log.e(TAG, "會員註冊失敗。");
//                        Common.toastShow(activity, "會員註冊失敗");
//                    }
//                }else {
//                    Common.toastShow(activity, R.string.textNoNetwork);
//                }
//
//            }
//        });

    }

    /* 處理拍照、
     * 選擇相簿照片、
     * 截圖
     * 等意圖 */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_TAKE_PIC:
                    crop(contentUri);
                    break;
                case REQ_PICK_PIC:
                    crop(intent.getData());
                    break;
                case REQ_CROP_PIC:
                    Uri uri = intent.getData();// getData 為得到 intent 所含資料的路徑
                    Bitmap bitmap = null;

                    if (uri != null) {
                        try {
                            bitmap = BitmapFactory.decodeStream(
                                    activity.getContentResolver().openInputStream(uri));// 從 uri 讀取的檔案,編碼轉檔成為 bitmap 格式
                            if (bitmap != null) {
                                imageView.setImageBitmap(round(bitmap));
                            }
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);// 把 bitmap 壓縮進 out
                            image = out.toByteArray();
                            /* 拍照結果存在宣告的實體變數 image
                             * 在這頁的每個地方都可以使用 image
                             * bitmap 作為在 App 顯示圖片用，轉成 image 是要送出並儲存在 Server 的
                             */
                        } catch (FileNotFoundException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                    if (bitmap != null) {
                        imageView.setImageBitmap(round(bitmap));
                    } else {
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_no_image);
                        imageView.setImageBitmap(round(bitmap));
                    }
                    break;
            }
        }
    }

    private void crop(Uri sourceImageUri) {
        File file = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        file = new File(file, "picture_cropped.jpg");
        Uri uri = Uri.fromFile(file);
        // 開啟截圖功能
        Intent intent = new Intent("com.android.camera.action.CROP");
        // 授權讓截圖程式可以讀取資料
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // 設定圖片來源與類型
        intent.setDataAndType(sourceImageUri, "image/*");
        // 設定要截圖
        intent.putExtra("crop", "true");
        // 設定截圖框大小
        // 0 代表能任意調整大小
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        // 設定圖片輸出寬高，0代表維持原尺寸
        intent.putExtra("outputX", 450);
        intent.putExtra("outputY", 450);
        // 是否保持原圖比例
        intent.putExtra("scale", true);
        // 設定截圖後圖片位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 設定是否要回傳值
        intent.putExtra("return-data", true);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // 開啟截圖activity
            startActivityForResult(intent, REQ_CROP_PIC);
        } else {
            Common.toastShow(activity,"找不到截圖程式");
        }
    }

    /* ==================== ⬇️以下方法並沒有使用到⬇️ ==================== */

    /* ==================== 兩種縮小照片的方法 ==================== */
    /**
     * 指定比例壓縮圖片
     * @param image     要壓縮的圖片
     * @param quality   壓縮的質量比
     */
    public static Bitmap compressImage(Bitmap image, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //質量壓縮方法，這裡 100 表示不壓縮，把壓縮後的資料存放到 baos 中
        image.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return image;
        //image大小不變，只是儲存的檔案變小了
    }

    /* 重設拍照圖片的尺寸大小 */
    public static Bitmap ResizeBitmap(Bitmap bitmap, int newWidth) {
        // 獲取原來圖片的寬高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 計算原來圖片的高寬之比
        float temp = ((float) height) / ((float) width);
        // 根據傳入的新圖片的寬度計算新圖片的高度
        int newHeight = (int) ((newWidth) * temp);

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // Bitmap 通過 matrix 矩陣變換生成新的 Bitmap
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

}
