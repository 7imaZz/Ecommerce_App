package com.shorbgy.ecommerceapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.CategoryAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentCategoriesBinding;
import com.shorbgy.ecommerceapp.pojo.Category;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.shorbgy.ecommerceapp.utils.OnCategoryItemSelected;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment implements OnCategoryItemSelected {

    private FragmentCategoriesBinding binding;
    private CategoryAdapter adapter;

    ArrayList<Category> categories = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_categories, container, false);
        adapter = new CategoryAdapter(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.categoriesRv.setAdapter(adapter);
        getCategories();
    }

    private void getCategories(){

        categories.clear();
        categories.add(new Category(getString(R.string.t_shirts), R.drawable.tshirts_category));
        categories.add(new Category(getString(R.string.sports_clothes), R.drawable.sports_category));
        categories.add(new Category(getString(R.string.female_clothes), R.drawable.female_category));
        categories.add(new Category(getString(R.string.jackets), R.drawable.jackets_category));
        categories.add(new Category(getString(R.string.glasses), R.drawable.glasses_category));
        categories.add(new Category(getString(R.string.hats), R.drawable.hats_category));
        categories.add(new Category(getString(R.string.bags), R.drawable.bags_category));
        categories.add(new Category(getString(R.string.shoes), R.drawable.shoes_category));
        categories.add(new Category(getString(R.string.headphones), R.drawable.headphones_category));
        categories.add(new Category(getString(R.string.laptops), R.drawable.laptops_category));
        categories.add(new Category(getString(R.string.accessories), R.drawable.accessories_category));
        categories.add(new Category(getString(R.string.phones), R.drawable.phones_category));

        adapter.setCategories(categories);
    }

    @Override
    public void setOnClickListener(int pos) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.CATEGORY_NAME, categories.get(pos).getCategory());

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_categoriesFragment_to_categoryProductsFragment2, bundle);
    }
}