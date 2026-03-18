//package com.codex.foodcaf.adapter;
//
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.codex.foodcaf.R;
//import com.codex.foodcaf.model.Order;
//
//import java.util.List;
//
//public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {
//
//    private List<Order> orderList;
//    private OnOrderClickListener listener;
//
//    public OrderHistoryAdapter(List<Order> orderList, OnOrderClickListener listener) {
//        this.orderList = orderList;
//        this.listener = listener;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Order order = orderList.get(position);
//
//        // ERROR FIX: itemName wenuwata tvRestaurantName id eka bawitha kara
//        holder.itemName.setText("#" + order.getOrderId());
//        holder.tvOrderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "N/A");
//        holder.tvStatus.setText(order.getStatus() != null ? order.getStatus() : "Pending");
//
//        if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Paid".equalsIgnoreCase(order.getStatus())) {
//            holder.tvStatus.setTextColor(Color.parseColor("#2EBA63"));
//            holder.iconStatus.setColorFilter(Color.parseColor("#2EBA63"));
//            holder.iconStatus.setImageResource(R.drawable.task_alt_24px);
//        } else if ("Pending".equalsIgnoreCase(order.getStatus())) {
//            holder.tvStatus.setTextColor(Color.parseColor("#FFC107"));
//            holder.iconStatus.setColorFilter(Color.parseColor("#FFC107"));
//            holder.iconStatus.setImageResource(android.R.drawable.ic_popup_sync);
//        } else {
//            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
//            holder.iconStatus.setColorFilter(Color.parseColor("#F44336"));
//            holder.iconStatus.setImageResource(android.R.drawable.ic_delete);
//        }
//
//        double total = 0;
//        if (order.getOrderItems() != null) {
//            for (Order.OrderItem item : order.getOrderItems()) {
//                total += item.getTotalPrice();
//            }
//        }
//        holder.tvTotalPrice.setText(String.format("LKR %.2f", total + 100)); // 100 Delivery fee
//
//        // List eke thiyena item wala expanded details eka hangala thiyanna (Uda card eke witharak pennanna)
//        if (holder.expandedDetailsLayout != null) {
//            holder.expandedDetailsLayout.setVisibility(View.GONE);
//        }
//
//        // List eken order ekak click karama main fragment ekata data pass kirima
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onOrderClick(order);
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return orderList.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView itemName, tvOrderDate, tvStatus, tvTotalPrice;
//        ImageView iconStatus;
//        View expandedDetailsLayout;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            // Methane ID eka R.id.itemName thibba eka R.id.tvRestaurantName kiyala haduwa
//            itemName = itemView.findViewById(R.id.itemName);
//            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
//            tvStatus = itemView.findViewById(R.id.tvStatus);
//            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
//            iconStatus = itemView.findViewById(R.id.iconStatus);
//            expandedDetailsLayout = itemView.findViewById(R.id.expandedDetailsLayout);
//        }
//    }
//
//    public interface OnOrderClickListener {
//        void onOrderClick(Order order);
//    }
//}

package com.codex.foodcaf.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private static final String TAG = "OrderImgDebug";

    private List<Order> orderList;
    private OnOrderClickListener listener;

    public OrderHistoryAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.itemName.setText("#" + order.getOrderId());
        holder.tvOrderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "N/A");
        holder.tvStatus.setText(order.getStatus() != null ? order.getStatus() : "Pending");

        // Status color + icon
        if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Paid".equalsIgnoreCase(order.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#2EBA63"));
            holder.iconStatus.setColorFilter(Color.parseColor("#2EBA63"));
            holder.iconStatus.setImageResource(R.drawable.task_alt_24px);
        } else if ("Pending".equalsIgnoreCase(order.getStatus())) {
            holder.tvStatus.setTextColor(Color.parseColor("#FFC107"));
            holder.iconStatus.setColorFilter(Color.parseColor("#FFC107"));
            holder.iconStatus.setImageResource(android.R.drawable.ic_popup_sync);
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            holder.iconStatus.setColorFilter(Color.parseColor("#F44336"));
            holder.iconStatus.setImageResource(android.R.drawable.ic_delete);
        }

        // Total price
        double total = 0;
        if (order.getOrderItems() != null) {
            for (Order.OrderItem item : order.getOrderItems()) {
                total += item.getTotalPrice();
            }
        }
        holder.tvTotalPrice.setText(String.format("LKR %.2f", total + 100));

        // ✅ item_order_history.xml eke imgRestaurant — default @drawable/order
        // Firestore eken productImage load karanawa
        holder.imgRestaurant.setImageResource(R.drawable.order); // default

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            String productId = order.getOrderItems().get(0).getProductId();
            Log.d(TAG, "▶ Order: " + order.getOrderId() + " | productId: " + productId);

            if (productId != null && !productId.isEmpty()) {
                holder.imgRestaurant.setTag(productId);
                Glide.with(holder.imgRestaurant.getContext()).clear(holder.imgRestaurant);
                holder.imgRestaurant.setImageResource(R.drawable.order); // recycle wena wita default

                FirebaseFirestore.getInstance()
                        .collection("products")
                        .document(productId)
                        .get()
                        .addOnSuccessListener(doc -> {
                            // ✅ Recycle fix — tag check
                            if (!productId.equals(holder.imgRestaurant.getTag())) return;

                            if (!doc.exists()) {
                                Log.e(TAG, "✖ Product doc NOT found: " + productId);
                                return;
                            }

                            List<String> images = (List<String>) doc.get("productImage");
                            if (images == null || images.isEmpty()) {
                                Log.e(TAG, "✖ productImage null/empty: " + productId);
                                return;
                            }

                            Log.d(TAG, "✔ Loading: " + images.get(0));
                            Glide.with(holder.imgRestaurant.getContext())
                                    .load(images.get(0))
                                    .placeholder(R.drawable.order)
                                    .error(R.drawable.order)
                                    .centerCrop()
                                    .into(holder.imgRestaurant);
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "✖ Firestore error: " + e.getMessage()));
            }
        }

        if (holder.expandedDetailsLayout != null) {
            holder.expandedDetailsLayout.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClick(order);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, tvOrderDate, tvStatus, tvTotalPrice;
        ImageView iconStatus, imgRestaurant;
        View expandedDetailsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName              = itemView.findViewById(R.id.itemName);
            tvOrderDate           = itemView.findViewById(R.id.tvOrderDate);
            tvStatus              = itemView.findViewById(R.id.tvStatus);
            tvTotalPrice          = itemView.findViewById(R.id.tvTotalPrice);
            iconStatus            = itemView.findViewById(R.id.iconStatus);
            imgRestaurant         = itemView.findViewById(R.id.imgRestaurant);  // item_order_history.xml
            expandedDetailsLayout = itemView.findViewById(R.id.expandedDetailsLayout);
        }
    }

    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }
}