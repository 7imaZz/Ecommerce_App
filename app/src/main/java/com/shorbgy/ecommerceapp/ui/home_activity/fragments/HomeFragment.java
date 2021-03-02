package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
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


public class HomeFragment extends Fragment implements OnProductItemsSelected, SearchView.OnQueryTextListener{

    private ProductAdapter adapter;
    private HomeViewModel viewModel;


    ArrayList<Product> products = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.home));

        if (!HomeActivity.isAdmin) {
            HomeActivity.navigationView.setCheckedItem(R.id.nav_home);
        }

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        FragmentHomeBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        adapter = new ProductAdapter(this);

        getProducts();
        binding.productRv.setAdapter(adapter);

        return binding.getRoot();
    }

    private void getProducts(){

        viewModel.productMutableLiveData.observe(getViewLifecycleOwner(), mProducts -> {
            products = mProducts;
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onItemSelected(int pos) {

        if (!HomeActivity.isAdmin) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.PRODUCT, adapter.getProducts().get(pos));

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_nav_home_to_itemFragment, bundle);
        }else{
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.PRODUCT, adapter.getProducts().get(pos));

            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    .navigate(R.id.action_nav_home_to_adminItemFragment, bundle);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        final MenuItem searchItem = menu.findItem(R.id.search_menu);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Product> filteredProducts = new ArrayList<>();

        if (!products.isEmpty()) {
            for (Product product : products) {
                if (product.getProduct_name().toLowerCase().contains(newText.toLowerCase())) {
                    filteredProducts.add(product);
                }
            }
            adapter.setProducts(filteredProducts);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}