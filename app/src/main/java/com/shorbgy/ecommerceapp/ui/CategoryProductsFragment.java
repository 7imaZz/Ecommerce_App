package com.shorbgy.ecommerceapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class CategoryProductsFragment extends Fragment implements OnProductItemsSelected{

    private static final String TAG = "CategoryProductFragment";

    private FragmentHomeBinding binding;
    private ProductAdapter adapter;

    ArrayList<Product> products = new ArrayList<>();
    private String categoryName = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        categoryName = getArguments().getString(Constants.CATEGORY_NAME);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        adapter = new ProductAdapter(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.productRv.setAdapter(adapter);
        getProducts();
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
                    if (categoryName.equals(product.getCategory())) {
                        products.add(product);
                    }
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
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.PRODUCT, adapter.getProducts().get(pos));

        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_categoryProductsFragment_to_itemFragment, bundle);
    }
}