package com.g2.runningFront.RunActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.g2.runningFront.R;
import com.g2.runningFront.SettingActivity.SettingActivity;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView btbRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        holdNavigraph();
        setTitle(R.string.run_name);

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
        NavController navCtrRun = Navigation.findNavController(MainActivity.this, R.id.fgNavigrath);
        NavigationUI.setupWithNavController(btbRun, navCtrRun);
        int i = btbRun.getSelectedItemId();

    }
}
