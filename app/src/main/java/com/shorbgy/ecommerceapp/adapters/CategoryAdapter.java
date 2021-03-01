package com.shorbgy.ecommerceapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.CategoryItemBinding;
import com.shorbgy.ecommerceapp.pojo.Category;
import com.shorbgy.ecommerceapp.utils.OnCategoryItemSelected;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        CategoryItemBinding binding;
        public CategoryViewHolder(@NonNull View itemView, CategoryItemBinding binding) {
            super(itemView);
            this.binding = binding;
        }
    }

    OnCategoryItemSelected onCategoryItemSelected;
    ArrayList<Category> categories = new ArrayList<>();

    public CategoryAdapter(OnCategoryItemSelected onCategoryItemSelected) {
        this.onCategoryItemSelected = onCategoryItemSelected;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater =
                (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        CategoryItemBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.category_item, parent, false);
        return new CategoryViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        holder.binding.categoryTv.setText(categories.get(position).getCategory());
        Picasso.get()
                .load(categories.get(position).getImageResource())
                .resize(480, 200)
                .centerCrop()
                .into(holder.binding.coverPhoto);
        holder.itemView.setOnClickListener(v -> onCategoryItemSelected.setOnClickListener(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
