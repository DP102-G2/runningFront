package com.g2.runningFront.SettingActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.g2.runningFront.R;
import com.g2.runningFront.RunActivity.MainActivity;
import com.g2.runningFront.ShopActivity.ShopActivity;
import com.g2.runningFront.SignInActivity.SignInActivity;

public class SettingActivity extends AppCompatActivity {
    Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle(R.string.setting_name);

        button = findViewById(R.id.set_btLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                intent = new Intent(SettingActivity.this, SignInActivity.class);
                startActivity(intent);

                //SettingActivity.this.finish();
                //前面區塊，根據要關閉的activity做更換

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);
        menu.removeItem(R.id.opSetting);
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
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                return true;
            case R.id.opSetting:
                return true;

        }
        return true;
    }
}