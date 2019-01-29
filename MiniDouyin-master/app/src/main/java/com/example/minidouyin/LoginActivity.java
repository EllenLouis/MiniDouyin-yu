package com.example.minidouyin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class LoginActivity extends AppCompatActivity {

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2019/1/28 加入动画？
        SharedPreferences lock = getSharedPreferences("lock",MODE_PRIVATE);
        SharedPreferences.Editor editor = getSharedPreferences("lock",MODE_PRIVATE).edit();
        String judge = lock.getString("user_name","");
        if(judge.equals("")){
            startActivity(new Intent(LoginActivity.this, UserInfoActivity.class));
        }
        else  startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}

