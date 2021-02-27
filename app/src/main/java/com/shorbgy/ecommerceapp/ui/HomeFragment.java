package com.shorbgy.ecommerceapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.ProductAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentHomeBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.shorbgy.ecommerceapp.utils.OnProductItemsSelected;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment implements OnProductItemsSelected, SearchView.OnQueryTextListener{

    private static final String TAG = "HomeFragment";

    private FragmentHomeBinding binding;
    private ProductAdapter adapter;



    ArrayList<Product> products = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();

        if (!HomeActivity.isAdmin) {
            HomeActivity.navigationView.setCheckedItem(R.id.nav_home);
        }


        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        adapter = new ProductAdapter(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getProducts();
        binding.productRv.setAdapter(adapter);
    }

    private void getProducts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Products");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    assert product != null;
                    products.add(product);
                }

                adapter.setProducts(products);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
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