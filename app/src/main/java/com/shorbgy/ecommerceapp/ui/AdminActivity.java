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
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.t_shirts));
            startActivity(intent);
        });

        binding.sportsShirtsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.sports_clothes));
            startActivity(intent);
        });

        binding.dressesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.female_clothes));
            startActivity(intent);
        });

        binding.jacketsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.jackets));
            startActivity(intent);
        });

        binding.glassesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.glasses));
            startActivity(intent);
        });

        binding.hatsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.hats));
            startActivity(intent);
        });

        binding.bagsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.bags));
            startActivity(intent);
        });

        binding.shoesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.shoes));
            startActivity(intent);
        });

        binding.headphonesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.headphones));
            startActivity(intent);
        });

        binding.laptopsImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.laptops));
            startActivity(intent);
        });

        binding.watchesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.accessories));
            startActivity(intent);
        });

        binding.phonesImage.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminCategory.class);
            intent.putExtra(Constants.CATEGORY_NAME, getString(R.string.phones));
            startActivity(intent);
        });

        binding.checkNewProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminOrdersActivity.class);
            startActivity(intent);
        });

        binding.editProductsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(Constants.IS_ADMIN, true);
            startActivity(intent);
        });
    }
}