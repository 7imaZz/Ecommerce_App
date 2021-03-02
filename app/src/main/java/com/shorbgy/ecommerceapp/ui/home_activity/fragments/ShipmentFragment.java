package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentShipmentBinding;
import com.shorbgy.ecommerceapp.pojo.Cart;
import com.shorbgy.ecommerceapp.pojo.Order;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.shorbgy.ecommerceapp.utils.Constants;
import java.util.ArrayList;
import java.util.Objects;

public class ShipmentFragment extends Fragment {

    private FragmentShipmentBinding binding;
    private ArrayList<Cart> products;
    private HomeViewModel viewModel;

    private int totalPrice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        assert getArguments() != null;
        products = getArguments().getParcelableArrayList(Constants.PRODUCTS_IN_CART);
        totalPrice = getArguments().getInt(Constants.TOTAL_PRICE);


        viewModel = ((HomeActivity) requireActivity()).viewModel;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shipment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        completeUserDetails();

        binding.confirmButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.usernameEt.getText())){
                Toast.makeText(requireActivity(), "Please Enter Your Name", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.emailEt.getText())){
                Toast.makeText(requireActivity(), "Please Enter Your Email", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.phoneNumberEt.getText())){
                Toast.makeText(requireActivity(), "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.cityNameEt.getText())){
                Toast.makeText(requireActivity(), "Please Enter Your City", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.homeAddressEt.getText())){
                Toast.makeText(requireActivity(), "Please Enter Your Home Address", Toast.LENGTH_SHORT).show();
            }else {
                confirmShipment();
            }
        });
    }

    private void showDialog(){
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);
        dialog.show();
    }

    private void confirmShipment(){

        Order order = new Order();
        order.setAddress(Objects.requireNonNull(binding.homeAddressEt.getText()).toString());
        order.setCity(Objects.requireNonNull(binding.cityNameEt.getText()).toString());
        order.setName(Objects.requireNonNull(binding.usernameEt.getText()).toString());
        order.setPhone(Objects.requireNonNull(binding.phoneNumberEt.getText()).toString());
        order.setState("Not Shipped");
        order.setTotal_price(totalPrice);
        order.setUid(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        viewModel.confirmShipment(products, order);

        viewModel.shipmentTaskMutableLiveData.observe(getViewLifecycleOwner(), task -> {
            if (task.isSuccessful()){
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.nav_home);
                showDialog();
            }
        });
    }

    private void completeUserDetails(){

        viewModel.userMutableLiveData.observe(getViewLifecycleOwner(), user -> {
            binding.usernameEt.setText(user.getName());
            binding.emailEt.setText(user.getEmail());
            binding.phoneNumberEt.setText(user.getPhone_number());
        });
    }
}