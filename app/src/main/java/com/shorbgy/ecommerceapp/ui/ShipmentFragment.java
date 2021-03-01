package com.shorbgy.ecommerceapp.ui;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentShipmentBinding;
import com.shorbgy.ecommerceapp.pojo.Cart;
import com.shorbgy.ecommerceapp.pojo.User;
import com.shorbgy.ecommerceapp.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ShipmentFragment extends Fragment {

    private static final String TAG = "ShipmentFragment";

    private FragmentShipmentBinding binding;
    private ArrayList<Cart> products;
    private int totalPrice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        products = getArguments().getParcelableArrayList(Constants.PRODUCTS_IN_CART);
        totalPrice = getArguments().getInt(Constants.TOTAL_PRICE);
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

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String transactionId = String.valueOf(System.currentTimeMillis());

        DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(transactionId);

        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference("Cart List")
                .child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String allProducts = "";
        for (Cart product: products){
            allProducts += product.getQuantity()+" "+product.getName()+" | ";
        }

        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("address", Objects.requireNonNull(binding.homeAddressEt.getText()).toString());
        orderMap.put("city", Objects.requireNonNull(binding.cityNameEt.getText()).toString());
        orderMap.put("date", saveCurrentDate);
        orderMap.put("name", Objects.requireNonNull(binding.usernameEt.getText()).toString());
        orderMap.put("phone", Objects.requireNonNull(binding.phoneNumberEt.getText()).toString());
        orderMap.put("state", "Not Shipped");
        orderMap.put("time", saveCurrentTime);
        orderMap.put("total_price", totalPrice);
        orderMap.put("products", allProducts);
        orderMap.put("transaction_id", transactionId);
        orderMap.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());

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

        decreaseProductsQuantity();
    }

    private void completeUserDetails(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                assert user != null;
                binding.usernameEt.setText(user.getName());
                binding.emailEt.setText(user.getEmail());
                binding.phoneNumberEt.setText(user.getPhone_number());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void decreaseProductsQuantity(){


        for (Cart product: products){

            int finalQuantity = Integer.parseInt(product.getTotal_pieces()) -
                    Integer.parseInt(product.getQuantity());

            DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("Products")
                    .child(product.getPid());

            if (finalQuantity>0) {
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("pieces", String.valueOf(finalQuantity));



                productReference.updateChildren(productMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: Successful");
                    }else {
                        Log.d(TAG, "onComplete: "+ Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
            }else {
                productReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(requireActivity(), "Succ", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: Successful");
                    }else {
                        Toast.makeText(requireActivity(), "Fail", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onComplete: "+ Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
            }
        }
    }
}