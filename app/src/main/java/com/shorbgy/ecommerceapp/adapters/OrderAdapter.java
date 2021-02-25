package com.shorbgy.ecommerceapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.OrderItemBinding;
import com.shorbgy.ecommerceapp.pojo.Order;
import com.shorbgy.ecommerceapp.utils.OnShipButtonClicked;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private final OrderItemBinding binding;
        public OrderViewHolder(@NonNull View itemView, OrderItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    ArrayList<Order> orders = new ArrayList<>();
    OnShipButtonClicked onShipButtonClicked;

    public OrderAdapter(OnShipButtonClicked onShipButtonClicked) {
        this.onShipButtonClicked = onShipButtonClicked;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderAdapter.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        OrderItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.order_item, parent, false);
        return new OrderViewHolder(binding.getRoot(), binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.OrderViewHolder holder, int position) {

        holder.binding.usernameTv.setText(orders.get(position).getName());
        holder.binding.orderTime.setText("Order Time: "+orders.get(position).getDate()
                +", "+orders.get(position).getTime());
        holder.binding.transactionIdTv.setText(orders.get(position).getTransaction_id());
        holder.binding.phoneNumberTv.setText(orders.get(position).getPhone());
        holder.binding.productPrice.setText("$"+orders.get(position).getTotal_price());
        holder.binding.locationTv.setText(orders.get(position).getAddress()+", "+orders.get(position).getCity());
        holder.binding.productsTv.setText(orders.get(position).getProducts());

        if (orders.get(position).getState().equals("Not Shipped")){
            holder.binding.stateNotShippedTv.setVisibility(View.VISIBLE);
            holder.binding.stateShippedTv.setVisibility(View.GONE);
        }else{
            holder.binding.stateNotShippedTv.setVisibility(View.GONE);
            holder.binding.stateShippedTv.setVisibility(View.VISIBLE);
            holder.binding.shipButton.setVisibility(View.GONE);
        }

        holder.binding.shipButton.setOnClickListener(v -> onShipButtonClicked.setOnClickListener(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
}
