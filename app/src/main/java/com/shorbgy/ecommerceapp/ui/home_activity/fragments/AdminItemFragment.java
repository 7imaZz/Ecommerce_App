package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.material.snackbar.Snackbar;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentAdminItemBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Objects;

public class AdminItemFragment extends Fragment {

    private FragmentAdminItemBinding binding;
    private Product product;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_item, container, false);

        Bundle bundle = getArguments();

        assert bundle != null;
        product = bundle.getParcelable(Constants.PRODUCT);

        binding.productNam.setText(product.getProduct_name());
        binding.productPrice.setText(product.getPrice());
        binding.productDesc.setText(product.getDescription());
        binding.pieces.setText(product.getPieces());

        Picasso.get()
                .load(product.getImage_url())
                .into(binding.productImage);

        binding.updateTv.setOnClickListener(v -> updateProduct());

        binding.closeTv.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack());

        binding.deleteButton.setOnClickListener(v -> deleteProduct());

        return binding.getRoot();
    }

    private void updateProduct(){


        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("product_name", Objects.requireNonNull(binding.productNam.getText()).toString());
        productMap.put("price", Objects.requireNonNull(binding.productPrice.getText()).toString());
        productMap.put("description", Objects.requireNonNull(binding.productDesc.getText()).toString());
        productMap.put("pieces", Objects.requireNonNull(binding.pieces.getText()).toString());

        viewModel.updateProduct(product, productMap);

        viewModel.updateProductTaskMutableLiveData.observe(getViewLifecycleOwner(), task -> {
            Snackbar.make(requireView(), "Product Updated Successfully", Snackbar.LENGTH_SHORT).show();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
            viewModel.updateProductTaskMutableLiveData.removeObservers(getViewLifecycleOwner());
        });
    }

    private void deleteProduct(){

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());

        dialog.setTitle("Confirmation");
        dialog.setMessage("Are you sure you want to delete this product?");

        dialog.setPositiveButton("Yes", (dialog1, which) ->{

            viewModel.deleteProduct(product);

            viewModel.deleteProductTaskMutableLiveData.observe(getViewLifecycleOwner(), task -> {
                if (task.isSuccessful()){
                    Snackbar.make(requireView(), "Product Deleted Successfully", Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
                    viewModel.deleteProductTaskMutableLiveData.removeObservers(getViewLifecycleOwner());
                }
            });
        });

        dialog.setNegativeButton("Cancel", (dialog12, which) -> dialog12.dismiss());

        dialog.show();
    }
}