//package com.codex.foodcaf.fragment;
//
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//
//import androidx.activity.OnBackPressedCallback;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.codex.foodcaf.R;
//import com.codex.foodcaf.adapter.OrderHistoryAdapter;
//import com.codex.foodcaf.model.Order;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class OrderFragment extends Fragment {
//
//    private static final String TAG = "OrderImgDebug";
//
//    private RecyclerView rvOrderHistory;
//    private OrderHistoryAdapter adapter;
//    private List<Order> orderList = new ArrayList<>();
//
//    private TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
//    private ImageView iconStatus;
//    private ImageView imgRestaurant2;
//    private LinearLayout expandedDetailsLayout;
//    private View btnRepeatOrder;
//
//    public OrderFragment() {}
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_order, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        rvOrderHistory        = view.findViewById(R.id.rvOrderHistory);
//        tvOrderId             = view.findViewById(R.id.orderId);
//        tvOrderDate           = view.findViewById(R.id.ItemOrderDate);
//        tvOrderStatus         = view.findViewById(R.id.OrderStatus);
//        tvOrderTotal          = view.findViewById(R.id.OrderTotalPrice);
//        iconStatus            = view.findViewById(R.id.iconStatus);
//        imgRestaurant2        = view.findViewById(R.id.imgRestaurant2);
//        expandedDetailsLayout = view.findViewById(R.id.expandedDetailsLayout);
//        btnRepeatOrder        = view.findViewById(R.id.btnRepeatOrder);
//
//        imgRestaurant2.setImageResource(R.drawable.order);
//
//        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        // ✅ Back press — Home fragment ekata yawanawa
//        requireActivity().getOnBackPressedDispatcher().addCallback(
//                getViewLifecycleOwner(),
//                new OnBackPressedCallback(true) {
//                    @Override
//                    public void handleOnBackPressed() {
//                        goToHome();
//                    }
//                }
//        );
//
//        // ✅ User logged in nathi nam Toast + Home ekata navigate
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            Toast.makeText(getContext(),
//                    "Please login to view your orders",
//                    Toast.LENGTH_SHORT).show();
//            goToHome();
//            return;
//        }
//
//        loadOrdersFromFirebase();
//    }
//
//    // ✅ Home fragment ekata navigate karana common method
//    private void goToHome() {
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragmentContainer, new HomeFragment())
//                .commit();
//    }
//
//    private void loadOrdersFromFirebase() {
//        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        FirebaseFirestore.getInstance()
//                .collection("orders")
//                .whereEqualTo("userId", uid)
//                .get()
//                .addOnSuccessListener(snapshots -> {
//                    if (!isAdded()) return;
//
//                    if (snapshots.isEmpty()) {
//                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    orderList = snapshots.toObjects(Order.class);
//
//                    java.util.Collections.sort(orderList, (o1, o2) -> {
//                        if (o1.getOrderDate() == null || o2.getOrderDate() == null) return 0;
//                        return o2.getOrderDate().compareTo(o1.getOrderDate());
//                    });
//
//                    adapter = new OrderHistoryAdapter(orderList, this::updateTopOrderDetails);
//                    rvOrderHistory.setAdapter(adapter);
//
//                    updateTopOrderDetails(orderList.get(0));
//                })
//                .addOnFailureListener(e -> {
//                    if (!isAdded()) return;
//                    Toast.makeText(getContext(),
//                            "Failed to load orders: " + e.getMessage(),
//                            Toast.LENGTH_LONG).show();
//                });
//    }
//
//    private void updateTopOrderDetails(Order order) {
//        tvOrderId.setText("#" + order.getOrderId());
//        tvOrderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "N/A");
//        tvOrderStatus.setText(order.getStatus());
//
//        if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Paid".equalsIgnoreCase(order.getStatus())) {
//            tvOrderStatus.setTextColor(Color.parseColor("#2EBA63"));
//            iconStatus.setColorFilter(Color.parseColor("#2EBA63"));
//            iconStatus.setImageResource(R.drawable.task_alt_24px);
//        } else if ("Pending".equalsIgnoreCase(order.getStatus())) {
//            tvOrderStatus.setTextColor(Color.parseColor("#FFC107"));
//            iconStatus.setColorFilter(Color.parseColor("#FFC107"));
//            iconStatus.setImageResource(android.R.drawable.ic_popup_sync);
//        } else {
//            tvOrderStatus.setTextColor(Color.parseColor("#F44336"));
//            iconStatus.setColorFilter(Color.parseColor("#F44336"));
//            iconStatus.setImageResource(android.R.drawable.ic_delete);
//        }
//
//        imgRestaurant2.setImageResource(R.drawable.order);
//
//        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
//            String productId = order.getOrderItems().get(0).getProductId();
//            Log.d(TAG, "▶ Top card productId: " + productId);
//
//            if (productId != null && !productId.isEmpty()) {
//                FirebaseFirestore.getInstance()
//                        .collection("products")
//                        .document(productId)
//                        .get()
//                        .addOnSuccessListener(doc -> {
//                            if (!isAdded() || imgRestaurant2 == null) return;
//                            if (!doc.exists()) return;
//                            List<String> images = (List<String>) doc.get("productImage");
//                            if (images == null || images.isEmpty()) return;
//                            Glide.with(requireContext())
//                                    .load(images.get(0))
//                                    .placeholder(R.drawable.order)
//                                    .error(R.drawable.order)
//                                    .centerCrop()
//                                    .into(imgRestaurant2);
//                        })
//                        .addOnFailureListener(e ->
//                                Log.e(TAG, "✖ Top card error: " + e.getMessage()));
//            }
//        }
//
//        expandedDetailsLayout.removeAllViews();
//        double total = 0;
//
//        if (order.getOrderItems() != null) {
//            for (Order.OrderItem item : order.getOrderItems()) {
//                total += item.getTotalPrice();
//
//                LinearLayout row = new LinearLayout(getContext());
//                row.setOrientation(LinearLayout.HORIZONTAL);
//                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                rowParams.setMargins(0, dpToPx(6), 0, dpToPx(6));
//                row.setLayoutParams(rowParams);
//
//                TextView tvName = new TextView(getContext());
//                tvName.setLayoutParams(new LinearLayout.LayoutParams(
//                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
//                tvName.setText(item.getProductName() + " x " + item.getQty());
//                tvName.setTextColor(Color.parseColor("#888888"));
//                tvName.setTextSize(13f);
//
//                TextView tvPrice = new TextView(getContext());
//                tvPrice.setLayoutParams(new LinearLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//                tvPrice.setText(String.format("LKR %.2f", item.getTotalPrice()));
//                tvPrice.setTextColor(Color.parseColor("#888888"));
//                tvPrice.setTextSize(13f);
//
//                row.addView(tvName);
//                row.addView(tvPrice);
//                expandedDetailsLayout.addView(row);
//            }
//        }
//
//        tvOrderTotal.setText(String.format("LKR %.2f", total + 100));
//
//        if (btnRepeatOrder != null) {
//            if (btnRepeatOrder.getParent() != null) {
//                ((ViewGroup) btnRepeatOrder.getParent()).removeView(btnRepeatOrder);
//            }
//            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(52));
//            btnParams.topMargin = dpToPx(16);
//            btnRepeatOrder.setLayoutParams(btnParams);
//            expandedDetailsLayout.addView(btnRepeatOrder);
//
//            btnRepeatOrder.setOnClickListener(v ->
//                    Toast.makeText(getContext(),
//                            "Repeat Order: #" + order.getOrderId(),
//                            Toast.LENGTH_SHORT).show()
//            );
//        }
//    }
//
//    private int dpToPx(int dp) {
//        float density = getResources().getDisplayMetrics().density;
//        return Math.round((float) dp * density);
//    }
//}


package com.codex.foodcaf.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.OrderHistoryAdapter;
import com.codex.foodcaf.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private static final String TAG = "OrderImgDebug";

    private RecyclerView rvOrderHistory;
    private OrderHistoryAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    private TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
    private ImageView iconStatus;
    private ImageView imgRestaurant2;
    private LinearLayout expandedDetailsLayout;
    private View btnRepeatOrder;

    public OrderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvOrderHistory        = view.findViewById(R.id.rvOrderHistory);
        tvOrderId             = view.findViewById(R.id.orderId);
        tvOrderDate           = view.findViewById(R.id.ItemOrderDate);
        tvOrderStatus         = view.findViewById(R.id.OrderStatus);
        tvOrderTotal          = view.findViewById(R.id.OrderTotalPrice);
        iconStatus            = view.findViewById(R.id.iconStatus);
        imgRestaurant2        = view.findViewById(R.id.imgRestaurant2);
        expandedDetailsLayout = view.findViewById(R.id.expandedDetailsLayout);
        btnRepeatOrder        = view.findViewById(R.id.btnRepeatOrder);

        imgRestaurant2.setImageResource(R.drawable.order);

        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        // ✅ Back press — Home fragment ekata yawanawa
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        goToHome();
                    }
                }
        );

        // ✅ User logged in nathi nam Toast + Home ekata navigate
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(),
                    "Please login to view your orders",
                    Toast.LENGTH_SHORT).show();
            goToHome();
            return;
        }

        loadOrdersFromFirebase();
    }

    // ✅ Home fragment ekata navigate karana common method
    private void goToHome() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new HomeFragment())
                .commit();
    }

    private void loadOrdersFromFirebase() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("orders")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;

                    if (snapshots.isEmpty()) {
                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    orderList = snapshots.toObjects(Order.class);

                    java.util.Collections.sort(orderList, (o1, o2) -> {
                        if (o1.getOrderDate() == null || o2.getOrderDate() == null) return 0;
                        return o2.getOrderDate().compareTo(o1.getOrderDate());
                    });

                    adapter = new OrderHistoryAdapter(orderList, this::updateTopOrderDetails);
                    rvOrderHistory.setAdapter(adapter);

                    updateTopOrderDetails(orderList.get(0));
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(),
                            "Failed to load orders: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateTopOrderDetails(Order order) {
        tvOrderId.setText("#" + order.getOrderId());
        tvOrderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "N/A");
        tvOrderStatus.setText(order.getStatus());

        if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Paid".equalsIgnoreCase(order.getStatus())) {
            tvOrderStatus.setTextColor(Color.parseColor("#2EBA63"));
            iconStatus.setColorFilter(Color.parseColor("#2EBA63"));
            iconStatus.setImageResource(R.drawable.task_alt_24px);
        } else if ("Pending".equalsIgnoreCase(order.getStatus())) {
            tvOrderStatus.setTextColor(Color.parseColor("#FFC107"));
            iconStatus.setColorFilter(Color.parseColor("#FFC107"));
            iconStatus.setImageResource(android.R.drawable.ic_popup_sync);
        } else {
            tvOrderStatus.setTextColor(Color.parseColor("#F44336"));
            iconStatus.setColorFilter(Color.parseColor("#F44336"));
            iconStatus.setImageResource(android.R.drawable.ic_delete);
        }

        imgRestaurant2.setImageResource(R.drawable.order);

        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            String productId = order.getOrderItems().get(0).getProductId();
            Log.d(TAG, "▶ Top card productId: " + productId);

            if (productId != null && !productId.isEmpty()) {
                FirebaseFirestore.getInstance()
                        .collection("products")
                        .document(productId)
                        .get()
                        .addOnSuccessListener(doc -> {
                            if (!isAdded() || imgRestaurant2 == null) return;
                            if (!doc.exists()) return;
                            List<String> images = (List<String>) doc.get("productImage");
                            if (images == null || images.isEmpty()) return;
                            Glide.with(requireContext())
                                    .load(images.get(0))
                                    .placeholder(R.drawable.order)
                                    .error(R.drawable.order)
                                    .centerCrop()
                                    .into(imgRestaurant2);
                        })
                        .addOnFailureListener(e ->
                                Log.e(TAG, "✖ Top card error: " + e.getMessage()));
            }
        }

        expandedDetailsLayout.removeAllViews();
        double total = 0;

        if (order.getOrderItems() != null) {
            for (Order.OrderItem item : order.getOrderItems()) {
                total += item.getTotalPrice();

                LinearLayout row = new LinearLayout(getContext());
                row.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                rowParams.setMargins(0, dpToPx(6), 0, dpToPx(6));
                row.setLayoutParams(rowParams);

                TextView tvName = new TextView(getContext());
                tvName.setLayoutParams(new LinearLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tvName.setText(item.getProductName() + " x " + item.getQty());
                tvName.setTextColor(Color.parseColor("#888888"));
                tvName.setTextSize(13f);

                TextView tvPrice = new TextView(getContext());
                tvPrice.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tvPrice.setText(String.format("LKR %.2f", item.getTotalPrice()));
                tvPrice.setTextColor(Color.parseColor("#888888"));
                tvPrice.setTextSize(13f);

                row.addView(tvName);
                row.addView(tvPrice);
                expandedDetailsLayout.addView(row);
            }
        }

        tvOrderTotal.setText(String.format("LKR %.2f", total + 100));

        if (btnRepeatOrder != null) {
            if (btnRepeatOrder.getParent() != null) {
                ((ViewGroup) btnRepeatOrder.getParent()).removeView(btnRepeatOrder);
            }
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(52));
            btnParams.topMargin = dpToPx(16);
            btnRepeatOrder.setLayoutParams(btnParams);
            expandedDetailsLayout.addView(btnRepeatOrder);

            btnRepeatOrder.setOnClickListener(v ->
                    Toast.makeText(getContext(),
                            "Repeat Order: #" + order.getOrderId(),
                            Toast.LENGTH_SHORT).show()
            );
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}