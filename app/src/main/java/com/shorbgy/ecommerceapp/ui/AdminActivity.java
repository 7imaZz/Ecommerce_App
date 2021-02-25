package com.shorbgy.ecommerceapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.ActivityAdminBinding;
import com.shorbgy.ecommerceapp.utils.Constants;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAdminBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_admin);

        binding.shirtsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.SHIRTS);
            startActivity(intent);
        });

        binding.sportsShirtsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.SPORT_SHIRTS);
            startActivity(intent);
        });

        binding.dressesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.DRESSES);
            startActivity(intent);
        });

        binding.jacketsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.JACKETS);
            startActivity(intent);
        });

        binding.glassesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.GLASSES);
            startActivity(intent);
        });

        binding.hatsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.HATS);
            startActivity(intent);
        });

        binding.bagsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.BAGS);
            startActivity(intent);
        });

        binding.shoesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.SHOES);
            startActivity(intent);
        });

        binding.headphonesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.HEADPHONES);
            startActivity(intent);
        });

        binding.laptopsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.LAPTOPS);
            startActivity(intent);
        });

        binding.watchesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.WATCHES);
            startActivity(intent);
        });

        binding.phonesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, Constants.PHONES);
            startActivity(intent);
        });

        binding.checkNewProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminOrdersActivity.class);
            startActivity(intent);
        });
    }
}