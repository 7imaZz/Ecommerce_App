package com.shorbgy.ecommerceapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentAdminItemBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class AdminItemFragment extends Fragment {

    private FragmentAdminItemBinding binding;
    private DatabaseReference databaseReference;
    private Product product;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_item, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();

        assert bundle != null;
        product = bundle.getParcelable(Constants.PRODUCT);

        binding.productNam.setText(product.getProduct_name());
        binding.productPrice.setText(product.getPrice());
        binding.productDesc.setText(product.getDescription());
        Picasso.get()
                .load(product.getImage_url())
                .into(binding.productImage);

        binding.updateTv.setOnClickListener(v -> updateProduct());

        binding.closeTv.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack());

        binding.deleteButton.setOnClickListener(v -> deleteProduct());
    }

    private void updateProduct(){


        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("product_name", Objects.requireNonNull(binding.productNam.getText()).toString());
        productMap.put("price", Objects.requireNonNull(binding.productPrice.getText()).toString());
        productMap.put("description", Objects.requireNonNull(binding.productDesc.getText()).toString());

        databaseReference.child(product.getPid()).updateChildren(productMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Snackbar.make(requireView(), "Product Updated Successfully", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
            }
        });
    }

    private void deleteProduct(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        dialog.setTitle("Confirmation");
        dialog.setMessage("Are you sure you want to delete this product?");

        dialog.setPositiveButton("Yes", (dialog1, which) -> databaseReference.child(product.getPid()).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Snackbar.make(requireView(), "Product Deleted Successfully", Snackbar.LENGTH_SHORT).show();
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
            }
        }));

        dialog.setNegativeButton("Cancel", (dialog12, which) -> dialog12.dismiss());

        dialog.show();
    }
}