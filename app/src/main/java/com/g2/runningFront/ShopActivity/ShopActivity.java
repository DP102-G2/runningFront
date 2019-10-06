package com.g2.runningFront.ShopActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.g2.runningFront.Common.Common;
import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.MainActivity;
import com.g2.runningFront.SettingActivity.SettingActivity;
import com.g2.runningFront.ShopActivity.ShopCart.TapPay.ShopCartAcpayFragment;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ShopActivity extends AppCompatActivity {
    private BottomNavigationView btbShop;
    private PaymentData paymentData;
    public static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 101;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        holdNavigraph();
        setTitle(R.string.shop_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        menu.removeItem(R.id.opShop);
        return true;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_PAYMENT_DATA_REQUEST_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    Common.toastShow(this, "GPAY 成功登陸資料");
                    paymentData = PaymentData.getFromIntent(data);
                    ShopCartAcpayFragment.paymentData = paymentData;
                    break;
                case RESULT_CANCELED:
                    Common.toastShow(this, "GPAY 取消");
                    break;
                default:
                    Common.toastShow(this, "GPAY 錯誤");
                    break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.opShop:

                return true;
            case R.id.opRun:
                intent = new Intent(ShopActivity.this, MainActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.opSetting:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                this.finish();
                return true;
        }
        return true;
    }


    private void holdNavigraph() {
        btbShop = findViewById(R.id.btbShop);
        NavController navCtrShop = Navigation.findNavController(ShopActivity.this, R.id.Shop_fgNavigrath);
        NavigationUI.setupWithNavController(btbShop, navCtrShop);

    }
}
