package com.shorbgy.ecommerceapp.ui.admin_orders_activity;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.pojo.Order;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class AdminOrdersViewModel extends ViewModel{

    private static final String TAG = "AdminOrdersViewModel";

    private final DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Users");
    private final ArrayList<Order> orders = new ArrayList<>();
    private final HashSet<String> transactionIds = new HashSet<>();


    public MutableLiveData<ArrayList<Order>> ordersMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Task<Void>> shipOrderTaskMutableLiveData = new MutableLiveData<>();


    public AdminOrdersViewModel() {
        getOrders();
    }

    public void getOrders(){

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                    DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference("Orders")
                            .child(Objects.requireNonNull(dataSnapshot.getKey()));

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
                                ordersMutableLiveData.postValue(orders);
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
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    public void shipOrder(String transactionId, String uid){

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("Orders")
                .child(uid)
                .child(transactionId);

        HashMap<String, Object> stateMap = new HashMap<>();
        stateMap.put("state", "Shipped");

        usersReference.updateChildren(stateMap).addOnCompleteListener(task ->
                shipOrderTaskMutableLiveData.postValue(task));
    }
}
