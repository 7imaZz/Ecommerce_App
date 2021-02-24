package com.shorbgy.ecommerceapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.CartAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentCartBinding;
import com.shorbgy.ecommerceapp.pojo.Cart;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";

    private CartAdapter adapter;
    private FragmentCartBinding binding;

    private final ArrayList<Cart> products = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        adapter = new CartAdapter();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getProducts();
        binding.productsRv.setAdapter(adapter);
    }

    private void getProducts(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart List")
                .child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Cart product = dataSnapshot.getValue(Cart.class);

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
}