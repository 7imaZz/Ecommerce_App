package com.shorbgy.ecommerceapp.ui;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentShipmentBinding;
import com.shorbgy.ecommerceapp.pojo.Cart;
import com.shorbgy.ecommerceapp.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ShipmentFragment extends Fragment {

    private FragmentShipmentBinding binding;
    private ArrayList<Cart> products;
    private int totalPrice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        products = getArguments().getParcelableArrayList(Constants.PRODUCTS_IN_CART);
        totalPrice = getArguments().getInt(Constants.TOTAL_PRICE);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_shipment, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());

        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(saveCurrentDate+saveCurrentTime);

        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("Cart List")
                .child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String allProducts = "";
        for (Cart product: products){
            allProducts += product.getName()+"|";
        }

        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("address", binding.homeAddressEt.getText().toString());
        orderMap.put("city", binding.cityNameEt.getText().toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("name", binding.usernameEt.getText().toString());
        orderMap.put("phone", binding.phoneNumberEt.getText().toString());
        orderMap.put("state", "Not Shipped");
        orderMap.put("time", saveCurrentTime);
        orderMap.put("total_price", totalPrice);
        orderMap.put("products", allProducts);
        orderMap.put("transaction_id", saveCurrentDate+saveCurrentTime);

        ordersReference.setValue(orderMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                cartReference.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){

                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();

                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                                .navigate(R.id.nav_home);
                        showDialog();
                    }
                });
            }
        });

    }
}