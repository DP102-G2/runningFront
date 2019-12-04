package com.g2.runningFront.RunActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.g2.runningFront.R;
import com.g2.runningFront.SettingActivity.SettingActivity;
import com.g2.runningFront.ShopActivity.Service.CommonService.CustomerService;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public BottomNavigationView btbRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holdNavigraph();
        setTitle(R.string.run_name);
        Intent intent =new Intent(this, CustomerService.class);
        startService(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        menu.removeItem(R.id.opRun);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.opShop:
                intent = new Intent(this, ShopActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.opRun:

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
        btbRun = findViewById(R.id.btbRun);
        NavController navCtrRun = Navigation.findNavController(this, R.id.fgNavigrath);
        NavigationUI.setupWithNavController(btbRun, navCtrRun);

    }

}
