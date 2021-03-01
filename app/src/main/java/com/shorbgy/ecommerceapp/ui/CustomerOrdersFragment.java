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
import com.shorbgy.ecommerceapp.adapters.CustomerOrderAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentCustomerOrdersBinding;
import com.shorbgy.ecommerceapp.pojo.Order;

import java.util.ArrayList;
import java.util.Objects;

public class CustomerOrdersFragment extends Fragment {

    private static final String TAG = "CustomerOrdersFragment";

    private FragmentCustomerOrdersBinding binding;
    private CustomerOrderAdapter adapter;

    private final ArrayList<Order> orders = new ArrayList<>();
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_customer_orders, container, false);
        adapter = new CustomerOrderAdapter(requireContext());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.ordersRv.setAdapter(adapter);
        getOrders();
    }

    private void getOrders(){
        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Order order = dataSnapshot.getValue(Order.class);
                        assert order != null;
                        orders.add(order);
                    }
                    adapter.setOrders(orders);
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
}