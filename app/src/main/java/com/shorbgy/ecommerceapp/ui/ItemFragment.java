package com.shorbgy.ecommerceapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentItemBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ItemFragment extends Fragment {

    private FragmentItemBinding binding;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private Product product;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference("Cart List");
        auth = FirebaseAuth.getInstance();

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
        product = bundle.getParcelable(Constants.PRODUCT);

        binding.productName.setText(product.getProduct_name());
        binding.productPrice.setText("$"+product.getPrice());
        binding.productDesc.setText(product.getDescription());

        Picasso.get()
                .load(product.getImage_url())
                .placeholder(R.drawable.product_placeholder)
                .into(binding.productImage);

        binding.fab.setOnClickListener(v -> addItemToCartList());
    }

    private void addItemToCartList(){

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("date", saveCurrentDate);
        productMap.put("discount", "");
        productMap.put("pid", product.getPid());
        productMap.put("name", product.getProduct_name());
        productMap.put("price", product.getPrice());
        productMap.put("quantity", 1);
        productMap.put("time", saveCurrentTime);
        productMap.put("image_url", product.getImage_url());

        databaseReference.child("Admin View").child(auth.getCurrentUser().getUid()).child("Products")
                .child(product.getPid())
                .setValue(productMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        databaseReference.child("User View").child(auth.getCurrentUser().getUid()).child("Products")
                                .child(product.getPid())
                                .setValue(productMap).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        Snackbar.make(requireView(),
                                                "Item Added To Cart", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

    }
}