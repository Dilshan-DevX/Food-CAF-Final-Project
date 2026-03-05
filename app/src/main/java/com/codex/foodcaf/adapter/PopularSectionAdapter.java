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

public class PopularSectionAdapter extends RecyclerView.Adapter<PopularSectionAdapter.ViewHolder> {

    private List<Product> products;
    private OnListingItemClickListener listener;

    public PopularSectionAdapter(List<Product> products, OnListingItemClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PopularSectionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularSectionAdapter.ViewHolder holder, int position) {
        Product  product = products.get(position);

        holder.foodTitle.setText(product.getFoodTitle());
        holder.foodRating.setText(product.getFoodRating());
        holder.foodPrice.setText("Rs "+product.getProductPrice());


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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.imgPopImage);
            foodTitle = itemView.findViewById(R.id.txtPopTitle);
            foodRating = itemView.findViewById(R.id.txtPopRating);
            foodPrice = itemView.findViewById(R.id.txtPopPrice);


        }
    }

    public interface OnListingItemClickListener {
        void onListingItemClick(Product product);
    }

}
