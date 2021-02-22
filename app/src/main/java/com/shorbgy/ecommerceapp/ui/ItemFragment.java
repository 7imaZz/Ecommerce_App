package com.shorbgy.ecommerceapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentItemBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ItemFragment extends Fragment {

    private FragmentItemBinding binding;

    private int quantity = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        assert bundle != null;
        Product product = bundle.getParcelable(Constants.PRODUCT);

        binding.productName.setText(product.getProduct_name());
        binding.productCategory.setText(product.getCategory());
        binding.productPrice.setText(product.getPrice() + "$");
        binding.productDesc.setText(product.getDescription());
        binding.quantity.setText(String.valueOf(quantity));

        Picasso.get()
                .load(product.getImage_url())
                .placeholder(R.drawable.product_placeholder)
                .into(binding.productImage);

        binding.increase.setOnClickListener(v -> {
            quantity++;
            binding.quantity.setText(String.valueOf(quantity));
        });

        binding.decrease.setOnClickListener(v -> {
            quantity--;
            binding.quantity.setText(String.valueOf(quantity));
        });
    }
}