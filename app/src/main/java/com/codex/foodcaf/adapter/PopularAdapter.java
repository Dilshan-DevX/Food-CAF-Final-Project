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

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.ViewHolder> {

    private List<Product> list;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(Product product);
    }

    public PopularAdapter(List<Product> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_popular, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product p = list.get(position);

        holder.title.setText(p.getFoodTitle());
        holder.price.setText("LKR " + p.getProductPrice());
        holder.rating.setText(p.getFoodRating());


        if (p.getProductImage() != null && !p.getProductImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(p.getProductImage().get(0))
                    .into(holder.image);
        }


        holder.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, rating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgPopImage);
            title = itemView.findViewById(R.id.txtPopTitle);
            price = itemView.findViewById(R.id.txtPopPrice);
            rating = itemView.findViewById(R.id.txtPopRating);
        }
    }
}