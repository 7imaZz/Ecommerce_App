package com.shorbgy.ecommerceapp.ui.admin_orders_activity;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.OrderAdapter;
import com.shorbgy.ecommerceapp.databinding.ActivityAdminOrdersBinding;
import com.shorbgy.ecommerceapp.pojo.Order;
import com.shorbgy.ecommerceapp.utils.OnShipButtonClicked;
import java.util.ArrayList;

public class AdminOrdersActivity extends AppCompatActivity implements OnShipButtonClicked {

    private AdminOrdersViewModel viewModel;
    private ActivityAdminOrdersBinding binding;
    private OrderAdapter adapter;

    private ArrayList<Order> orders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_orders);

        viewModel = new ViewModelProvider(this).get(AdminOrdersViewModel.class);

        adapter = new OrderAdapter(this);

        binding.ordersRv.setAdapter(adapter);

        getOrders();
    }

    private void getOrders(){

        viewModel.ordersMutableLiveData.observe(this, mOrders -> {
            orders = mOrders;
            adapter.setOrders(orders);
            adapter.notifyDataSetChanged();
        });
    }

    private void shipOrder(String transactionId, String uid){

        viewModel.shipOrder(transactionId, uid);

        viewModel.shipOrderTaskMutableLiveData.observe(this, task -> {

            if (task.isSuccessful()){
                Snackbar.make(binding.getRoot(), "Shipped Successfully", Snackbar.LENGTH_SHORT).show();
            }

            viewModel.shipOrderTaskMutableLiveData.removeObservers(this);
        });
    }

    @Override
    public void setOnClickListener(int pos) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle("Confirmation");
        dialog.setMessage("Are This Order Shipped Successfully?");

        dialog.setPositiveButton("Yes", (dialog1, which) -> {
            shipOrder(orders.get(pos).getTransaction_id(), orders.get(pos).getUid());
            orders.get(pos).setState("Shipped");
            adapter.notifyDataSetChanged();
        });

        dialog.setNegativeButton("No", (dialog12, which) -> dialog12.dismiss());

        dialog.show();

    }
}