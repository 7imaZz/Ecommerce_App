package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import com.google.android.material.snackbar.Snackbar;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentItemBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class ItemFragment extends Fragment {

    private HomeViewModel viewModel;
    private FragmentItemBinding binding;
    private Product product;


    private int quantity = 1;
    private long totalPrice = 0;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_item, container, false);

        Bundle bundle = getArguments();

        assert bundle != null;
        product = bundle.getParcelable(Constants.PRODUCT);

        totalPrice = Long.parseLong(product.getPrice());

        binding.productName.setText(product.getProduct_name());
        binding.productPrice.setText("Total Price: $"+totalPrice);
        binding.productDesc.setText(product.getDescription());
        binding.quantity.setText(String.valueOf(quantity));
        binding.pieces.setText(product.getPieces()+" Pieces Existing");

        Picasso.get()
                .load(product.getImage_url())
                .placeholder(R.drawable.product_placeholder)
                .into(binding.productImage);

        binding.increase.setOnClickListener(v -> {
            if (quantity<Integer.parseInt(product.getPieces())){
                quantity++;
                binding.quantity.setText(String.valueOf(quantity));
                totalPrice = quantity*Long.parseLong(product.getPrice());
                binding.productPrice.setText("Total Price: $"+totalPrice);
            }else {
                Toast.makeText(requireActivity(), "There Are Only "+product.getPieces()+" Pieces"
                        , Toast.LENGTH_SHORT).show();
            }
        });

        binding.decrease.setOnClickListener(v -> {
            if (quantity>1) {
                quantity--;
                binding.quantity.setText(String.valueOf(quantity));
                totalPrice = quantity*Long.parseLong(product.getPrice());
                binding.productPrice.setText("Total Price: $"+totalPrice);
            }
        });

        binding.addToCart.setOnClickListener(v -> addItemToCartList());

        return binding.getRoot();
    }

    private void addItemToCartList(){

        viewModel.addItemToCartList(product, String.valueOf(totalPrice), String.valueOf(quantity));

        viewModel.addItemToCartTaskMutableLiveData.observe(getViewLifecycleOwner(), task -> {
            if (task.isSuccessful()){
                Snackbar.make(requireView(),
                        "Item Added To Cart", Snackbar.LENGTH_SHORT).show();
                viewModel.addItemToCartTaskMutableLiveData.removeObservers(getViewLifecycleOwner());
            }
        });
    }


}