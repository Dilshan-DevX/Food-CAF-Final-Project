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
import com.codex.foodcaf.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class CatListAdapter extends RecyclerView.Adapter<CatListAdapter.ViewHolder> {

    private List<Product> products;
    private OnListingItemClickListener listener;

    private FirebaseStorage storage;


    public CatListAdapter(List<Product> products, OnListingItemClickListener listener) {
        this.products = products;
        this.listener = listener;
        storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public CatListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatListAdapter.ViewHolder holder, int position) {

        Product  product = products.get(position);

        holder.foodTitle.setText(product.getFoodTitle());
        holder.foodRating.setText(product.getFoodRating());
        holder.foodTime.setText(product.getFoodTime());
        holder.foodDetail.setText(product.getIngrideint());
        holder.foodPrice.setText("Rs "+product.getProductPrice()+"0");
        holder.foodTime.setText(product.getFoodTime());
        if (product.isAvailability()) {
            holder.availability.setColorFilter(android.graphics.Color.parseColor("#52C85A"));
        } else {
            holder.availability.setColorFilter(android.graphics.Color.parseColor("#F44336"));
        }

//            Glide.with(holder.itemView.getContext())
//                    .load(product.getProductImage().get(0))
////                    .circleCrop()
//                    .into(holder.foodImage);

        /// ////////////////////////////////////////////////////////////////////

        if (product.getProductImage() != null && !product.getProductImage().isEmpty() && product.getProductImage().get(0).startsWith("http")) {


            Glide.with(holder.itemView.getContext())
                    .load(product.getProductImage().get(0))
                    .into(holder.foodImage);

        } else if (product.getProductId() != null) {

            String storagePath = "/product-images/" + product.getProductId();

            storage.getReference(storagePath).listAll()
                    .addOnSuccessListener(listResult -> {
                        if (!listResult.getItems().isEmpty()) {

                            java.util.List<com.google.android.gms.tasks.Task<android.net.Uri>> tasks = new java.util.ArrayList<>();
                            for (com.google.firebase.storage.StorageReference item : listResult.getItems()) {
                                tasks.add(item.getDownloadUrl());
                            }

                            com.google.android.gms.tasks.Tasks.whenAllSuccess(tasks).addOnSuccessListener(objects -> {

                                java.util.List<String> downloadUrls = new java.util.ArrayList<>();
                                for (Object obj : objects) {
                                    downloadUrls.add(obj.toString()); // ලින්ක් ටික Array එකකට දාගන්නවා
                                }

                                // 1. UI එකේ පෙන්නන්න පළවෙනි පින්තූරය Glide එකට දෙනවා
                                if (!downloadUrls.isEmpty()) {
                                    Glide.with(holder.itemView.getContext())
                                            .load(downloadUrls.get(0))
                                            .into(holder.foodImage);
                                }

                                // 2. Firestore එකේ ඩේටා එක Update කරනවා (පින්තූර ඔක්කොම)
                                com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                                db.collection("products")
                                        .whereEqualTo("productId", product.getProductId())
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            if (!querySnapshot.isEmpty()) {
                                                String documentId = querySnapshot.getDocuments().get(0).getId();

                                                // 🔴 Database එකට ලින්ක් ඔක්කොම ඇතුළත් Array එක යවනවා
                                                db.collection("products").document(documentId)
                                                        .update("productImage", downloadUrls);

                                                // Object එකත් අප්ඩේට් කරනවා
                                                product.setProductImage(downloadUrls);
                                            }
                                        });
                            });
                        }
                    });
        }

/////////////////////////////////////////////////////////////////////////////////

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
