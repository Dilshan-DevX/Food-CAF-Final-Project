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
import com.codex.foodcaf.model.CartItem;
import com.codex.foodcaf.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> cartItems;
    private OnListingItemClickListener listener;

    public CartAdapter(List<CartItem> cartItems, OnListingItemClickListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);

        double unitPrice = cartItem.getUnitPrice();

        holder.foodPrice.setText("LKR" + cartItem.getProductPrice());
        holder.foodQty.setText(String.format("%02d", cartItem.getQty()));


        if (cartItem.getAttributes() != null && !cartItem.getAttributes().isEmpty()) {
            String selectedPortion = cartItem.getAttributes().get(0).getValues().get(0);
            holder.foodPortion.setText(selectedPortion);
        } else {
            holder.foodPortion.setText("Regular");
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("productId", cartItem.getProductId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qd) {
                        if (!qd.isEmpty()) {
                            Product product = qd.getDocuments().get(0).toObject(Product.class);

                            // Title එක සෙට් කිරීම
                            holder.foodTitle.setText(product.getFoodTitle());

                            // පින්තූරය ලෝඩ් කිරීම
                            if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(product.getProductImage().get(0))
                                        .into(holder.foodImage);
                            }

                            holder.itemView.setOnClickListener(view -> {
                            });
                        }
                    }
                });



    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView foodImage;
        TextView foodTitle;
        TextView foodPortion;
        TextView foodPrice;
        TextView foodQty;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.imgCartItem);
            foodTitle = itemView.findViewById(R.id.txtCartItemTitle);
            foodPortion = itemView.findViewById(R.id.txtCartItemPortion);
            foodPrice = itemView.findViewById(R.id.txtCartItemPrice);
            foodQty = itemView.findViewById(R.id.txtCartQty);


        }
    }

    public interface OnListingItemClickListener {
        void onListingItemClick(Product product);
    }

}
