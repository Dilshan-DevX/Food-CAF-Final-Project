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
                                    downloadUrls.add(obj.toString());
                                }


                                if (!downloadUrls.isEmpty()) {
                                    Glide.with(holder.itemView.getContext())
                                            .load(downloadUrls.get(0))
                                            .into(holder.foodImage);
                                }


                                com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                                db.collection("products")
                                        .whereEqualTo("productId", product.getProductId())
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            if (!querySnapshot.isEmpty()) {
                                                String documentId = querySnapshot.getDocuments().get(0).getId();


                                                db.collection("products").document(documentId)
                                                        .update("productImage", downloadUrls);

                                                product.setProductImage(downloadUrls);
                                            }
                                        });
                            });
                        }
                    });
        }

/////////////////////////////////////////////////////////////////////////////////

        holder.btnAdd.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth auth = com.google.firebase.auth.FirebaseAuth.getInstance();

            if (auth.getCurrentUser() == null) {
                android.widget.Toast.makeText(v.getContext(), "Please login to add items to cart", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();
            com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();

            com.codex.foodcaf.model.CartItem cartItem = new com.codex.foodcaf.model.CartItem();
            cartItem.setProductId(product.getProductId());
            cartItem.setProductName(product.getFoodTitle());
            cartItem.setProductPrice(product.getProductPrice());
            cartItem.setUnitPrice(product.getProductPrice());
            cartItem.setQty(1);

            java.util.List<com.codex.foodcaf.model.CartItem.Attribute> attributes = new java.util.ArrayList<>();
            com.codex.foodcaf.model.CartItem.Attribute attr = new com.codex.foodcaf.model.CartItem.Attribute();
            attr.setValues(java.util.Collections.singletonList("Regular"));
            attr.setPrice(java.util.Collections.singletonList(String.valueOf(product.getProductPrice())));
            attributes.add(attr);

            cartItem.setAttributes(attributes);

            String docId = product.getProductId() + "_Regular";

            db.collection("users").document(uid).collection("cart")
                    .document(docId)
                    .set(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        android.widget.Toast.makeText(v.getContext(), "Added to Cart!", android.widget.Toast.LENGTH_SHORT).show();

                        android.content.Intent intent = new android.content.Intent("com.codex.foodcaf.CART_UPDATED");
                        intent.setPackage(v.getContext().getPackageName());
                        v.getContext().sendBroadcast(intent);
                    })
                    .addOnFailureListener(e -> {
                        android.widget.Toast.makeText(v.getContext(), "Failed to add", android.widget.Toast.LENGTH_SHORT).show();
                    });
        });


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
        com.google.android.material.card.MaterialCardView btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.foodImage);
            foodTitle = itemView.findViewById(R.id.foodTitle);
            foodRating = itemView.findViewById(R.id.foodRating);
            foodPrice = itemView.findViewById(R.id.foodPrice);
            foodTime = itemView.findViewById(R.id.foodTime);
            foodDetail = itemView.findViewById(R.id.foodCalories);
            availability = itemView.findViewById(R.id.avlb);
            btnAdd = itemView.findViewById(R.id.btnAdd);

        }
    }

    public interface OnListingItemClickListener {
        void onListingItemClick(Product product);
    }

}
