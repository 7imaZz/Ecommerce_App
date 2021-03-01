package com.shorbgy.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.CustomerOrderItemBinding;
import com.shorbgy.ecommerceapp.pojo.Order;

import java.util.ArrayList;

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.CustomerOrderViewHolder>{

    public static class CustomerOrderViewHolder extends RecyclerView.ViewHolder {
        CustomerOrderItemBinding binding;
        public CustomerOrderViewHolder(@NonNull View itemView, CustomerOrderItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    ArrayList<Order> orders = new ArrayList<>();
    Context context;

    public CustomerOrderAdapter(Context context) {
        this.context = context;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CustomerOrderAdapter.CustomerOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater)
                parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CustomerOrderItemBinding binding = DataBindingUtil
                .inflate(inflater, R.layout.customer_order_item, parent, false);
        return new CustomerOrderViewHolder(binding.getRoot(), binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerOrderAdapter.CustomerOrderViewHolder holder, int position) {

        String state = orders.get(position).getState();

        holder.binding.usernameTv.setText(orders.get(position).getName());
        holder.binding.dateTv.setText(orders.get(position).getDate()+", "+orders.get(position).getTime());
        holder.binding.transactionIdTv.setText("Transaction ID: "+orders.get(position).getTransaction_id());
        holder.binding.productsTv.setText("Products: "+orders.get(position).getProducts());
        holder.binding.totalPrice.setText("Total Price: $"+orders.get(position).getTotal_price());
        holder.binding.stateTv.setText(state);
        holder.binding.locationTv.setText(orders.get(position).getCity()+", "+orders.get(position).getAddress());

        if (state.equals("Not Shipped")){
            holder.binding.stateTv.setTextColor(ContextCompat.getColor(context, R.color.orange));
            holder.binding.stateTv.setText("Pending");
        }else {
            holder.binding.stateTv.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

}
