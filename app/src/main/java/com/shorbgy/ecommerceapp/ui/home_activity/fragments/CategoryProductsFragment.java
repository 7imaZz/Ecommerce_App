package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.ProductAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentHomeBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.shorbgy.ecommerceapp.utils.OnProductItemsSelected;

import java.util.ArrayList;
import java.util.Objects;

public class CategoryProductsFragment extends Fragment implements OnProductItemsSelected{

    private ProductAdapter adapter;
    private HomeViewModel viewModel;

    ArrayList<Product> products = new ArrayList<>();
    private String categoryName = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();


        assert getArguments() != null;
        categoryName = getArguments().getString(Constants.CATEGORY_NAME);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(categoryName);
        viewModel = ((HomeActivity) requireActivity()).viewModel;
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        adapter = new ProductAdapter(this);

        binding.productRv.setAdapter(adapter);
        getProducts();

        return binding.getRoot();
    }

    private void getProducts(){

        viewModel.getCategoryProducts(categoryName);
        viewModel.categoryProductsMutableLiveData.observe(getViewLifecycleOwner(), mProducts -> {
            products = mProducts;
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemSelected(int pos) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PRODUCT, adapter.getProducts().get(pos));

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_categoryProductsFragment_to_itemFragment, bundle);
    }
}