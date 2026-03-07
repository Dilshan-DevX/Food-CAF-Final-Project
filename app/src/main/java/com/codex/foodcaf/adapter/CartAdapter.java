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

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Product> products;
    private OnListingItemClickListener listener;

    public CartAdapter(List<Product> products, OnListingItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Product  product = products.get(position);

        holder.foodTitle.setText(product.getFoodTitle());
        holder.foodRating.setText(product.getFoodRating());
        holder.foodTime.setText(product.getFoodTime());
        holder.foodDetail.setText(product.getIngrideint());
        holder.foodPrice.setText("Rs "+product.getProductPrice());
        holder.foodTime.setText(product.getFoodTime());
        if (product.isAvailability()) {
            holder.availability.setColorFilter(android.graphics.Color.parseColor("#52C85A"));
        } else {
            holder.availability.setColorFilter(android.graphics.Color.parseColor("#F44336"));
        }

            Glide.with(holder.itemView.getContext())
                    .load(product.getProductImage().get(0))
//                    .circleCrop()
                    .into(holder.foodImage);

        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onListingItemClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodTitle;
        TextView foodRating;
        TextView foodPrice;
        TextView foodTime;
        TextView foodDetail;
        ImageView availability;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodRating = itemView.findViewById(R.id.foodRating);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodTime = itemView.findViewById(R.id.foodTime);
            foodDetail = itemView.findViewById(R.id.foodCalories);
            availability = itemView.findViewById(R.id.avlb);

        }
    }

    public interface OnListingItemClickListener {
        void onListingItemClick(Product product);
    }

}
