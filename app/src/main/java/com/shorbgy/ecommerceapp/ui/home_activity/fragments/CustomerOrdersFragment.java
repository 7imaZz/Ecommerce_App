package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.CustomerOrderAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentCustomerOrdersBinding;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;

import java.util.Objects;

public class CustomerOrdersFragment extends Fragment {

    private CustomerOrderAdapter adapter;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.orders));

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        FragmentCustomerOrdersBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_customer_orders, container, false);
        adapter = new CustomerOrderAdapter(requireContext());

        binding.ordersRv.setAdapter(adapter);
        getOrders();

        return binding.getRoot();
    }

    private void getOrders(){

        viewModel.getOrders();

        viewModel.ordersMutableLiveData.observe(getViewLifecycleOwner(), orders -> {
            adapter.setOrders(orders);
            adapter.notifyDataSetChanged();
        });
    }
}