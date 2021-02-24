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
import com.shorbgy.ecommerceapp.databinding.CartItemBinding;
import com.shorbgy.ecommerceapp.pojo.Cart;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private CartItemBinding binding;
        public CartViewHolder(@NonNull View itemView, CartItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    ArrayList<Cart> products = new ArrayList<>();

    public void setProducts(ArrayList<Cart> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CartItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.cart_item, parent, false);
        return new CartViewHolder(binding.getRoot(), binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {

        holder.binding.productName.setText(products.get(position).getName());
        holder.binding.quantity.setText("Quantity: "+products.get(position).getQuantity());
        holder.binding.price.setText("Price: $"+products.get(position).getPrice());

        Picasso.get()
                .load(products.get(position).getImage_url())
                .placeholder(R.drawable.product_placeholder)
                .resize(360, 240)
                .centerCrop()
                .into(holder.binding.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
