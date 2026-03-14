package com.codex.foodcaf.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.model.CartItem;
import com.codex.foodcaf.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeProductAdapter extends RecyclerView.Adapter<HomeProductAdapter.ViewHolder> {

    private List<Product> productList;
    private OnHomeProductClickListener listener;

    // 🔴 Constructor එකට Listener එකත් එකතු කළා
    public HomeProductAdapter(List<Product> productList, OnHomeProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getFoodTitle());
        holder.tvPrice.setText(String.format("LKR %.2f", product.getProductPrice()));
        holder.foodRating.setText(product.getFoodRating());
        holder.tvTime.setText(product.getFoodTime());

        if (product.isAvailability()) {
            holder.avlb.setColorFilter(android.graphics.Color.parseColor("#52C85A"));
        } else {
            holder.avlb.setColorFilter(android.graphics.Color.parseColor("#F44336"));
        }

        if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getProductImage().get(0))
                    .into(holder.imgProduct);
        }

        // 🔴 1. මුළු අයිටම් එකම (පින්තූරය/නම) ක්ලික් කළාම Single Page එකට යන්න
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });

        // 🔴 2. + (Add) බට්න් එක ක්ලික් කළාම Cart එකට Add වෙන්න
        holder.btnAdd.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (auth.getCurrentUser() == null) {
                Toast.makeText(v.getContext(), "Please login to add items to cart", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getProductId());
            cartItem.setProductName(product.getFoodTitle());
            cartItem.setProductPrice(product.getProductPrice());
            cartItem.setUnitPrice(product.getProductPrice());
            cartItem.setQty(1);

            // "Regular" කියන Default Attribute එක දානවා (කලින් විදියටම)
            List<CartItem.Attribute> attributes = new ArrayList<>();
            CartItem.Attribute attr = new CartItem.Attribute();
            attr.setValues(Collections.singletonList("Regular"));
            attr.setPrice(Collections.singletonList(String.valueOf(product.getProductPrice())));
            attributes.add(attr);

            cartItem.setAttributes(attributes);

            // Document ID එක "ID_Regular" විදියට හැදෙනවා
            String docId = product.getProductId() + "_Regular";

            db.collection("users").document(uid).collection("cart")
                    .document(docId)
                    .set(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(v.getContext(), "Added to Cart!", Toast.LENGTH_SHORT).show();

                        // Notification එකට Broadcast එක යවනවා
                        Intent intent = new Intent("com.codex.foodcaf.CART_UPDATED");
                        intent.setPackage(v.getContext().getPackageName());
                        v.getContext().sendBroadcast(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, avlb;
        TextView tvProductName, tvPrice, foodRating, tvTime;
        View btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            avlb = itemView.findViewById(R.id.avlb);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            foodRating = itemView.findViewById(R.id.foodRating);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnAdd = itemView.findViewById(R.id.btnAdd);
        }
    }

    // 🔴 Click Listener Interface එක
    public interface OnHomeProductClickListener {
        void onProductClick(Product product);
    }
}