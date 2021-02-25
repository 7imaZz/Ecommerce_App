package com.shorbgy.ecommerceapp.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
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
import com.shorbgy.ecommerceapp.utils.Constants;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private static final String TAG = "CartFragment";

    private CartAdapter adapter;
    private FragmentCartBinding binding;
    private int totalPrice;

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
        swipeToDelete();

        binding.buyNowButton.setOnClickListener(v -> {
            if (totalPrice>0){

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.PRODUCTS_IN_CART, products);
                bundle.putInt(Constants.TOTAL_PRICE, totalPrice);

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_cartFragment_to_shipmentFragment, bundle);
            }
        });
    }



    private void getProducts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Cart List")
                .child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Products");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalPrice = 0;
                products.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Cart product = dataSnapshot.getValue(Cart.class);

                    totalPrice += Integer.parseInt(product.getPrice());

                    assert product != null;
                    products.add(product);
                }

                adapter.setProducts(products);
                adapter.notifyDataSetChanged();
                binding.totalPrice.setText("$"+ totalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    private void deleteProduct(String pid){

        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference("Cart List").child("User View")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products")
                .child(pid);

        DatabaseReference adminReference = FirebaseDatabase.getInstance()
                .getReference("Cart List").child("Admin View")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products")
                .child(pid);

        userReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                adminReference.removeValue().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        Snackbar.make(requireView(), "Product Removed From Cart", Snackbar.LENGTH_LONG)
                                .show();
                    }
                });
            }else {
                Log.d(TAG, "onComplete: "+task.getException().getMessage());
            }
        });
    }

    private void swipeToDelete(){
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Cart cart = products.get(viewHolder.getAdapterPosition());
                deleteProduct(cart.getPid());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.productsRv);
    }
}