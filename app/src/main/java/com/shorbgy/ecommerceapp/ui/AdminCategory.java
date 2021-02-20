package com.shorbgy.ecommerceapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.utils.Constants;

public class AdminCategory extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        String categoryName = getIntent().getStringExtra(Constants.CATEGORY_NAME);
        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();
    }
}