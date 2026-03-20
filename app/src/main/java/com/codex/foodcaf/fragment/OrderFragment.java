////package com.codex.foodcaf.fragment;
////
////import android.graphics.Color;
////import android.os.Bundle;
////import android.util.Log;
////
////import androidx.activity.OnBackPressedCallback;
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.fragment.app.Fragment;
////import androidx.recyclerview.widget.LinearLayoutManager;
////import androidx.recyclerview.widget.RecyclerView;
////
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.ImageView;
////import android.widget.LinearLayout;
////import android.widget.TextView;
////import android.widget.Toast;
////
////import com.bumptech.glide.Glide;
////import com.codex.foodcaf.R;
////import com.codex.foodcaf.adapter.OrderHistoryAdapter;
////import com.codex.foodcaf.model.Order;
////import com.google.firebase.auth.FirebaseAuth;
////import com.google.firebase.firestore.FirebaseFirestore;
////
////import java.util.ArrayList;
////import java.util.List;
////
////public class OrderFragment extends Fragment {
////
////    private static final String TAG = "OrderImgDebug";
////
////    private RecyclerView rvOrderHistory;
////    private OrderHistoryAdapter adapter;
////    private List<Order> orderList = new ArrayList<>();
////
////    private TextView tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
////    private ImageView iconStatus;
////    private ImageView imgRestaurant2;
////    private LinearLayout expandedDetailsLayout;
////    private View btnRepeatOrder;
////
////    public OrderFragment() {}
////
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        return inflater.inflate(R.layout.fragment_order, container, false);
////    }
////
////    @Override
////    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////        super.onViewCreated(view, savedInstanceState);
////
////        rvOrderHistory        = view.findViewById(R.id.rvOrderHistory);
////        tvOrderId             = view.findViewById(R.id.orderId);
////        tvOrderDate           = view.findViewById(R.id.ItemOrderDate);
////        tvOrderStatus         = view.findViewById(R.id.OrderStatus);
////        tvOrderTotal          = view.findViewById(R.id.OrderTotalPrice);
////        iconStatus            = view.findViewById(R.id.iconStatus);
////        imgRestaurant2        = view.findViewById(R.id.imgRestaurant2);
////        expandedDetailsLayout = view.findViewById(R.id.expandedDetailsLayout);
////        btnRepeatOrder        = view.findViewById(R.id.btnRepeatOrder);
////
////        imgRestaurant2.setImageResource(R.drawable.order);
////
////        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getContext()));
////
////        // ✅ Back press — Home fragment ekata yawanawa
////        requireActivity().getOnBackPressedDispatcher().addCallback(
////                getViewLifecycleOwner(),
////                new OnBackPressedCallback(true) {
////                    @Override
////                    public void handleOnBackPressed() {
////                        goToHome();
////                    }
////                }
////        );
////
////        // ✅ User logged in nathi nam Toast + Home ekata navigate
////        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
////            Toast.makeText(getContext(),
////                    "Please login to view your orders",
////                    Toast.LENGTH_SHORT).show();
////            goToHome();
////            return;
////        }
////
////        loadOrdersFromFirebase();
////    }
////
////    // ✅ Home fragment ekata navigate karana common method
////    private void goToHome() {
////        requireActivity().getSupportFragmentManager()
////                .beginTransaction()
////                .replace(R.id.fragmentContainer, new HomeFragment())
////                .commit();
////    }
////
////    private void loadOrdersFromFirebase() {
////        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
////
////        FirebaseFirestore.getInstance()
////                .collection("orders")
////                .whereEqualTo("userId", uid)
////                .get()
////                .addOnSuccessListener(snapshots -> {
////                    if (!isAdded()) return;
////
////                    if (snapshots.isEmpty()) {
////                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
////                        return;
////                    }
////
////                    orderList = snapshots.toObjects(Order.class);
////
////                    java.util.Collections.sort(orderList, (o1, o2) -> {
////                        if (o1.getOrderDate() == null || o2.getOrderDate() == null) return 0;
////                        return o2.getOrderDate().compareTo(o1.getOrderDate());
////                    });
////
////                    adapter = new OrderHistoryAdapter(orderList, this::updateTopOrderDetails);
////                    rvOrderHistory.setAdapter(adapter);
////
////                    updateTopOrderDetails(orderList.get(0));
////                })
////                .addOnFailureListener(e -> {
////                    if (!isAdded()) return;
////                    Toast.makeText(getContext(),
////                            "Failed to load orders: " + e.getMessage(),
////                            Toast.LENGTH_LONG).show();
////                });
////    }
////
////    private void updateTopOrderDetails(Order order) {
////        tvOrderId.setText("#" + order.getOrderId());
////        tvOrderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "N/A");
////        tvOrderStatus.setText(order.getStatus());
////
////        if ("Delivered".equalsIgnoreCase(order.getStatus()) || "Paid".equalsIgnoreCase(order.getStatus())) {
////            tvOrderStatus.setTextColor(Color.parseColor("#2EBA63"));
////            iconStatus.setColorFilter(Color.parseColor("#2EBA63"));
////            iconStatus.setImageResource(R.drawable.task_alt_24px);
////        } else if ("Pending".equalsIgnoreCase(order.getStatus())) {
////            tvOrderStatus.setTextColor(Color.parseColor("#FFC107"));
////            iconStatus.setColorFilter(Color.parseColor("#FFC107"));
////            iconStatus.setImageResource(android.R.drawable.ic_popup_sync);
////        } else {
////            tvOrderStatus.setTextColor(Color.parseColor("#F44336"));
////            iconStatus.setColorFilter(Color.parseColor("#F44336"));
////            iconStatus.setImageResource(android.R.drawable.ic_delete);
////        }
////
////        imgRestaurant2.setImageResource(R.drawable.order);
////
////        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
////            String productId = order.getOrderItems().get(0).getProductId();
////            Log.d(TAG, "▶ Top card productId: " + productId);
////
////            if (productId != null && !productId.isEmpty()) {
////                FirebaseFirestore.getInstance()
////                        .collection("products")
////                        .document(productId)
////                        .get()
////                        .addOnSuccessListener(doc -> {
////                            if (!isAdded() || imgRestaurant2 == null) return;
////                            if (!doc.exists()) return;
////                            List<String> images = (List<String>) doc.get("productImage");
////                            if (images == null || images.isEmpty()) return;
////                            Glide.with(requireContext())
////                                    .load(images.get(0))
////                                    .placeholder(R.drawable.order)
////                                    .error(R.drawable.order)
////                                    .centerCrop()
////                                    .into(imgRestaurant2);
////                        })
////                        .addOnFailureListener(e ->
////                                Log.e(TAG, "✖ Top card error: " + e.getMessage()));
////            }
////        }
////
////        expandedDetailsLayout.removeAllViews();
////        double total = 0;
////
////        if (order.getOrderItems() != null) {
////            for (Order.OrderItem item : order.getOrderItems()) {
////                total += item.getTotalPrice();
////
////                LinearLayout row = new LinearLayout(getContext());
////                row.setOrientation(LinearLayout.HORIZONTAL);
////                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
////                        ViewGroup.LayoutParams.MATCH_PARENT,
////                        ViewGroup.LayoutParams.WRAP_CONTENT);
////                rowParams.setMargins(0, dpToPx(6), 0, dpToPx(6));
////                row.setLayoutParams(rowParams);
////
////                TextView tvName = new TextView(getContext());
////                tvName.setLayoutParams(new LinearLayout.LayoutParams(
////                        0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
////                tvName.setText(item.getProductName() + " x " + item.getQty());
////                tvName.setTextColor(Color.parseColor("#888888"));
////                tvName.setTextSize(13f);
////
////                TextView tvPrice = new TextView(getContext());
////                tvPrice.setLayoutParams(new LinearLayout.LayoutParams(
////                        ViewGroup.LayoutParams.WRAP_CONTENT,
////                        ViewGroup.LayoutParams.WRAP_CONTENT));
////                tvPrice.setText(String.format("LKR %.2f", item.getTotalPrice()));
////                tvPrice.setTextColor(Color.parseColor("#888888"));
////                tvPrice.setTextSize(13f);
////
////                row.addView(tvName);
////                row.addView(tvPrice);
////                expandedDetailsLayout.addView(row);
////            }
////        }
////
////        tvOrderTotal.setText(String.format("LKR %.2f", total + 100));
////
////        if (btnRepeatOrder != null) {
////            if (btnRepeatOrder.getParent() != null) {
////                ((ViewGroup) btnRepeatOrder.getParent()).removeView(btnRepeatOrder);
////            }
////            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
////                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(52));
////            btnParams.topMargin = dpToPx(16);
////            btnRepeatOrder.setLayoutParams(btnParams);
////            expandedDetailsLayout.addView(btnRepeatOrder);
////
////            btnRepeatOrder.setOnClickListener(v ->
////                    Toast.makeText(getContext(),
////                            "Repeat Order: #" + order.getOrderId(),
////                            Toast.LENGTH_SHORT).show()
////            );
////        }
////    }
////
////    private int dpToPx(int dp) {
////        float density = getResources().getDisplayMetrics().density;
////        return Math.round((float) dp * density);
////    }
////}
//
//
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

import android.app.AlertDialog;
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
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderFragment extends Fragment {

    private static final String TAG            = "OrderImgDebug";
    private static final long   CANCEL_WINDOW  = 10 * 60 * 1000L; // 10 minutes in ms

    private RecyclerView    rvOrderHistory;
    private OrderHistoryAdapter adapter;
    private List<Order>     orderList = new ArrayList<>();

    // ── Top card views ────────────────────────────────────────────────────────
    private TextView      tvOrderId, tvOrderDate, tvOrderStatus, tvOrderTotal;
    private ImageView     iconStatus, imgRestaurant2;
    private LinearLayout  expandedDetailsLayout;
    private View          btnRepeatOrder;

    // ── Currently selected order (cancel eke walata) ──────────────────────────
    private Order         selectedOrder;
    // Firestore eke document ID save karanawa (orderId field != document ID possibility)
    private String        selectedDocumentId;

    public OrderFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        // Back → Home
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override public void handleOnBackPressed() { goToHome(); }
                });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login to view your orders",
                    Toast.LENGTH_SHORT).show();
            goToHome();
            return;
        }

        loadOrdersFromFirebase();
    }

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

                    orderList.clear();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        Order o = doc.toObject(Order.class);

                        if (o.getOrderId() == null || o.getOrderId().isEmpty()) {
                            o.setOrderId(doc.getId());
                        }
                        orderList.add(o);
                    }

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
                    Toast.makeText(getContext(), "Failed: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    private void updateTopOrderDetails(Order order) {
        selectedOrder = order;

        tvOrderId.setText("#" + order.getOrderId());
        tvOrderDate.setText(order.getOrderDate() != null ? order.getOrderDate() : "N/A");
        tvOrderStatus.setText(order.getStatus());


        if ("Delivered".equalsIgnoreCase(order.getStatus())
                || "Paid".equalsIgnoreCase(order.getStatus())) {
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

        // Image
        imgRestaurant2.setImageResource(R.drawable.order);
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            String productId = order.getOrderItems().get(0).getProductId();
            if (productId != null && !productId.isEmpty()) {
                FirebaseFirestore.getInstance().collection("products").document(productId).get()
                        .addOnSuccessListener(doc -> {
                            if (!isAdded() || imgRestaurant2 == null || !doc.exists()) return;
                            List<String> images = (List<String>) doc.get("productImage");
                            if (images != null && !images.isEmpty()) {
                                Glide.with(requireContext()).load(images.get(0))
                                        .placeholder(R.drawable.order).error(R.drawable.order)
                                        .centerCrop().into(imgRestaurant2);
                            }
                        });
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
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            if (btnRepeatOrder.getParent() != null)
                ((ViewGroup) btnRepeatOrder.getParent()).removeView(btnRepeatOrder);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(52));
            btnParams.topMargin = dpToPx(16);
            btnRepeatOrder.setLayoutParams(btnParams);
            expandedDetailsLayout.addView(btnRepeatOrder);
            btnRepeatOrder.setOnClickListener(v ->
                    Toast.makeText(getContext(), "Repeat Order: #" + order.getOrderId(),
                            Toast.LENGTH_SHORT).show());
        }


        addCancelButtonIfEligible(order);
    }


    private void addCancelButtonIfEligible(Order order) {
        boolean isPending = "Pending".equalsIgnoreCase(order.getStatus());
        if (!isPending) return; // Pending nathi unoth cancel button nanna

        boolean withinWindow = isWithin10Minutes(order.getOrderDate());
        if (!withinWindow) return; // 10 min giya unoth cancel button nanna

        // ✅ Cancel button dynamically add karanawa
        MaterialButton btnCancel = new MaterialButton(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(52));
        params.topMargin = dpToPx(10);
        btnCancel.setLayoutParams(params);
        btnCancel.setText("CANCEL ORDER");
        btnCancel.setTextColor(Color.WHITE);
        btnCancel.setTextSize(13f);
        btnCancel.setCornerRadius(dpToPx(26));
        btnCancel.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(Color.parseColor("#F44336")));

        btnCancel.setOnClickListener(v -> showCancelConfirmDialog(order));

        expandedDetailsLayout.addView(btnCancel);
    }

    private boolean isWithin10Minutes(String orderDate) {
        if (orderDate == null) return false;


        String[] formats = {
                "yyyy/MM/dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "dd MMM yyyy - hh:mm a",
                "dd/MM/yyyy HH:mm:ss",
                "dd/MM/yyyy HH:mm",
                "MMM dd, yyyy HH:mm:ss",
        };

        for (String fmt : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt, Locale.getDefault());
                Date parsedDate = sdf.parse(orderDate);
                if (parsedDate != null) {
                    long diff = System.currentTimeMillis() - parsedDate.getTime();
                    return diff >= 0 && diff <= CANCEL_WINDOW;
                }
            } catch (ParseException ignored) {}
        }

        Log.e(TAG, "✖ Could not parse orderDate: " + orderDate);
        return false;
    }

    private void showCancelConfirmDialog(Order order) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel Order #" + order.getOrderId() + "?\n\nThis cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> cancelOrder(order))
                .setNegativeButton("No, Keep", null)
                .show();
    }

    private void cancelOrder(Order order) {
        String docId = order.getOrderId();

        FirebaseFirestore.getInstance()
                .collection("orders")
                .document(docId)
                .delete()
                .addOnSuccessListener(v -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(),
                            "Order #" + docId + " cancelled successfully.",
                            Toast.LENGTH_SHORT).show();


                    orderList.removeIf(o -> docId.equals(o.getOrderId()));
                    if (orderList.isEmpty()) {
                        expandedDetailsLayout.removeAllViews();
                        tvOrderId.setText("-");
                        tvOrderDate.setText("-");
                        tvOrderStatus.setText("-");
                        tvOrderTotal.setText("LKR 0.00");
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyDataSetChanged();
                        updateTopOrderDetails(orderList.get(0));
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(),
                            "Cancel failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}