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
import com.shorbgy.ecommerceapp.databinding.ProductItemBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.utils.OnProductItemsSelected;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ProductItemBinding binding;
        public ProductViewHolder(@NonNull View itemView, ProductItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    private OnProductItemsSelected onProductItemsSelected;
    private ArrayList<Product> products = new ArrayList<>();

    public ProductAdapter(OnProductItemsSelected onProductItemsSelected) {
        this.onProductItemsSelected = onProductItemsSelected;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ProductItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.product_item, parent, false);
        return new ProductViewHolder(binding.getRoot(), binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {

        holder.binding.productName.setText(products.get(position).getProduct_name());
        holder.binding.productPrice.setText("$"+products.get(position).getPrice());
        holder.binding.productDesc.setText(products.get(position).getDescription());

        Picasso.get()
                .load(products.get(position).getImage_url())
                .resize(360, 240)
                .centerCrop()
                .into(holder.binding.productImage);

        holder.itemView.setOnClickListener(v ->
                onProductItemsSelected.onItemSelected(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
