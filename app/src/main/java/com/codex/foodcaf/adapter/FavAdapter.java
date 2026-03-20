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
import com.codex.foodcaf.model.Product;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private List<Product> favList;
    private OnFavClickListener listener;

    public FavAdapter(List<Product> favList, OnFavClickListener listener) {
        this.favList = favList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = favList.get(position);

        holder.tvFavName.setText(product.getFoodTitle());
        holder.tvFavPrice.setText(String.format("LKR %.2f", product.getProductPrice()));
        holder.tvFavRating.setText(product.getFoodRating());
        holder.tvFavTime.setText("⏱ " + product.getFoodTime());

        if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getProductImage().get(0))
                    .centerCrop()
                    .into(holder.imgFavFood);
        }

        // View Product බට්න් එක
        holder.btnViewProduct.setOnClickListener(v -> {
            if (listener != null) listener.onViewClick(product);
        });

        // Remove බට්න් එක
        holder.removeBtn.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveClick(product, position);
        });
    }

    @Override
    public int getItemCount() {
        return favList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFavFood;
        TextView tvFavName, tvFavPrice, tvFavRating, tvFavTime;
        MaterialButton btnViewProduct, removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFavFood = itemView.findViewById(R.id.imgFavFood);
            tvFavName = itemView.findViewById(R.id.tvFavName);
            tvFavPrice = itemView.findViewById(R.id.tvFavPrice);
            tvFavRating = itemView.findViewById(R.id.tvFavRating);
            tvFavTime = itemView.findViewById(R.id.tvFavTime);
            btnViewProduct = itemView.findViewById(R.id.btnAddToCart); // id එක btnAddToCart කියලා තියෙන්නේ XML එකේ
            removeBtn = itemView.findViewById(R.id.removeBtn);
        }
    }

    public interface OnFavClickListener {
        void onViewClick(Product product);
        void onRemoveClick(Product product, int position);
    }
}