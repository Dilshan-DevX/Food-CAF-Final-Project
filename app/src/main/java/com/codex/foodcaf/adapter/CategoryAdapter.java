package com.codex.foodcaf.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.model.Category;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<Category> categories;
    private OnCategoryClickListener listener;

    private FirebaseStorage storage;

    public CategoryAdapter(List<Category> categories,OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        Category  category = categories.get(position);
        holder.categoryTitle.setText(category.getCategoryName());


        holder.categorySubtitle.setText(category.getCategorySubtitle());
            Glide.with(holder.itemView.getContext())
                    .load(category.getCategoryImage())
                    .centerCrop()
                    .into(holder.categoryImage);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView categoryImage;
        TextView categoryTitle;
        TextView categorySubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            categorySubtitle = itemView.findViewById(R.id.categorySubtitle);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

}
