package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.CategoryAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentCategoriesBinding;
import com.shorbgy.ecommerceapp.pojo.Category;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.shorbgy.ecommerceapp.utils.OnCategoryItemSelected;

import java.util.ArrayList;
import java.util.Objects;

public class CategoriesFragment extends Fragment implements OnCategoryItemSelected {

    private CategoryAdapter adapter;
    private HomeViewModel viewModel;

    ArrayList<Category> categories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.categories));

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        FragmentCategoriesBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        adapter = new CategoryAdapter(this);

        binding.categoriesRv.setAdapter(adapter);
        getCategories();

        return binding.getRoot();
    }


    private void getCategories(){
        viewModel.getCategories();

        viewModel.categoryMutableLiveData.observe(getViewLifecycleOwner(), mCategories -> {
            categories = mCategories;
            adapter.setCategories(categories);
        });
    }

    @Override
    public void setOnClickListener(int pos) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CATEGORY_NAME, categories.get(pos).getCategory());

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_categoriesFragment_to_categoryProductsFragment2, bundle);
    }
}