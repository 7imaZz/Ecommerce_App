package com.shorbgy.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import com.shorbgy.ecommerceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        binding.loginBtn.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        binding.joinNowBtn.setOnClickListener(v->
                startActivity(new Intent(this, RegisterActivity.class)));
    }
}