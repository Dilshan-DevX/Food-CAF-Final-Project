package com.codex.foodcaf.adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private CartItemInteractionListener listener;

    // 🔴 Interface එකේ නම වෙනස් කළා Cart එකට ගැළපෙන්න
    public CartAdapter(List<CartItem> cartItems, CartItemInteractionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);


        double unitPrice = cartItem.getUnitPrice();

        holder.foodPrice.setText(String.format("LKR %.2f", cartItem.getProductPrice()));
        holder.foodQty.setText(String.format("%02d", cartItem.getQty()));


        if (cartItem.getAttributes() != null && !cartItem.getAttributes().isEmpty()) {
            String selectedPortion = cartItem.getAttributes().get(0).getValues().get(0);
            holder.foodPortion.setText(selectedPortion);
        } else {
            holder.foodPortion.setText("Regular");
        }


        holder.btnCartPlus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION) {
                int currentQty = cartItem.getQty();
                currentQty++;

                cartItem.setQty(currentQty);
                cartItem.setProductPrice(currentQty * unitPrice);


                holder.foodQty.setText(String.format("%02d", currentQty));

                holder.foodPrice.setText(String.format("LKR %.2f", cartItem.getProductPrice()));

                if(listener != null){
                    listener.onQuantityUpdated(cartItem);
                }
            }
        });


        // Minus (-)
        holder.btnCartMinus.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION) {
                // හරියටම ක්ලික් කරපු අයිටම් එක ගන්නවා
                CartItem currentItem = cartItems.get(pos);
                int currentQty = currentItem.getQty();

                if (currentQty > 1) {
                    currentQty--;
                    currentItem.setQty(currentQty);
                    currentItem.setProductPrice(currentQty * unitPrice);

                    holder.foodQty.setText(String.format("%02d", currentQty));
                    holder.foodPrice.setText("LKR " + currentItem.getProductPrice());

                    if(listener != null){
                        listener.onQuantityUpdated(currentItem);
                    }
                } else {
                    new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("Remove Item")
                            .setMessage("Are you sure you want to remove this item from the cart?")
                            .setPositiveButton("Yes", (dialog, which) -> {


                                if(listener != null){
                                    listener.onItemRemoved(currentItem);
                                }

                                cartItems.remove(pos);
                                notifyItemRemoved(pos);
                                notifyItemRangeChanged(pos, cartItems.size());
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("productId", cartItem.getProductId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qd) {
                        if (!qd.isEmpty()) {
                            Product product = qd.getDocuments().get(0).toObject(Product.class);

                            holder.foodTitle.setText(product.getFoodTitle());

                            if (product.getProductImage() != null && !product.getProductImage().isEmpty()) {
                                Glide.with(holder.itemView.getContext())
                                        .load(product.getProductImage().get(0))
                                        .into(holder.foodImage);
                            }

                            holder.itemView.setOnClickListener(view -> {
                                if(listener != null) {
                                    listener.onItemClick(product);
                                }
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


        ImageView btnCartPlus;
        ImageView btnCartMinus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodImage = itemView.findViewById(R.id.imgCartItem);
            foodTitle = itemView.findViewById(R.id.txtCartItemTitle);
            foodPortion = itemView.findViewById(R.id.txtCartItemPortion);
            foodPrice = itemView.findViewById(R.id.txtCartItemPrice);
            foodQty = itemView.findViewById(R.id.txtCartQty);

            btnCartPlus = itemView.findViewById(R.id.btnCartPlus);
            btnCartMinus = itemView.findViewById(R.id.btnCartMinus);
        }
    }

    public interface CartItemInteractionListener {
        void onQuantityUpdated(CartItem cartItem); // Qty එක වෙනස් වුණාම
        void onItemRemoved(CartItem cartItem);     // Item එක Delete කළාම
        void onItemClick(Product product);         // Item එක Click කළාම
    }
}