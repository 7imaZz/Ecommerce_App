package com.shorbgy.ecommerceapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.OrderAdapter;
import com.shorbgy.ecommerceapp.pojo.Order;
import com.shorbgy.ecommerceapp.utils.OnShipButtonClicked;
import java.util.ArrayList;
import java.util.HashSet;

public class AdminOrdersActivity extends AppCompatActivity implements OnShipButtonClicked {

    private static final String TAG = "AdminOrdersActivity";

    private OrderAdapter adapter;

    private final ArrayList<Order> orders = new ArrayList<>();
    private final HashSet<String> transactionIds = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.shorbgy.ecommerceapp.databinding.ActivityAdminOrdersBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_orders);

        adapter = new OrderAdapter(this);

        binding.ordersRv.setAdapter(adapter);

        getOrders();
    }

    private void getOrders(){

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                    DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Orders")
                            .child(dataSnapshot.getKey());

                    ordersReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    Order order = dataSnapshot.getValue(Order.class);
                                    assert order != null;
                                    if (!transactionIds.contains(order.getTransaction_id())) {
                                        orders.add(order);
                                        transactionIds.add(order.getTransaction_id());
                                    }
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void setOnClickListener(int pos) {
        Toast.makeText(this, orders.get(pos).getName(), Toast.LENGTH_SHORT).show();
    }
}