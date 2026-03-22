package com.codex.foodcaf.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.CartAdapter;
import com.codex.foodcaf.databinding.FragmentCartBinding;
import com.codex.foodcaf.model.CartItem;
import com.codex.foodcaf.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;


public class CartFragment extends Fragment implements SensorEventListener {

    private FragmentCartBinding binding;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;
    private final double DELIVERY_FEE = 100.00;


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 800;
    private boolean isDialogShowing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            if (sensorManager != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String uid = firebaseAuth.getCurrentUser().getUid();


            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                cartItemList = queryDocumentSnapshots.toObjects(CartItem.class);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                binding.cartRecyclerView.setLayoutManager(layoutManager);

                                adapter = new CartAdapter(cartItemList, new CartAdapter.CartItemInteractionListener() {
                                    @Override
                                    public void onQuantityUpdated(CartItem cartItem) {
                                        if (cartItem.getProductId() == null || cartItem.getProductId().isEmpty()) return;

                                        String portion = "Regular";
                                        if (cartItem.getAttributes() != null && !cartItem.getAttributes().isEmpty()) {
                                            portion = cartItem.getAttributes().get(0).getValues().get(0);
                                        }
                                        String docId = cartItem.getProductId() + "_" + portion;

                                        db.collection("users").document(uid).collection("cart")
                                                .document(docId)
                                                .update("qty", cartItem.getQty(), "productPrice", cartItem.getProductPrice())
                                                .addOnSuccessListener(aVoid -> calculateTotal())
                                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onItemRemoved(CartItem cartItem) {
                                        if (cartItem.getProductId() == null || cartItem.getProductId().isEmpty()) return;

                                        String portion = "Regular";
                                        if (cartItem.getAttributes() != null && !cartItem.getAttributes().isEmpty()) {
                                            portion = cartItem.getAttributes().get(0).getValues().get(0);
                                        }
                                        String docId = cartItem.getProductId() + "_" + portion;

                                        db.collection("users").document(uid).collection("cart")
                                                .document(docId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(getContext(), "Item removed!", Toast.LENGTH_SHORT).show();
                                                    calculateTotal();
                                                });
                                    }

                                    @Override
                                    public void onItemClick(Product product) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("productId", product.getProductId());
                                        SingleProductFragment fragment = new SingleProductFragment();
                                        fragment.setArguments(bundle);
                                        getParentFragmentManager().beginTransaction()
                                                .replace(R.id.fragmentContainer, fragment)
                                                .addToBackStack(null).commit();
                                    }
                                });

                                binding.cartRecyclerView.setAdapter(adapter);
                                calculateTotal();
                            }
                        }
                    });
        }

        binding.btnCheckout.setOnClickListener(view1 -> {
            if (cartItemList == null || cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Please add items to your cart.", Toast.LENGTH_SHORT).show();
            } else {
                CheckOutFragment checkOutFragment = new CheckOutFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, checkOutFragment)
                        .addToBackStack(null).commit();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                } else {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new HomeFragment()).commit();
                }
            }
        });
    }

    private void calculateTotal() {
        if (cartItemList == null || cartItemList.isEmpty()) {
            binding.txtSubTotal.setText("LKR 0.00");
            binding.txtDeliveryFee.setText("LKR 0.00");
            binding.txtTotalCost.setText("LKR 0.00");
            return;
        }
        double subTotal = 0;
        for (CartItem item : cartItemList) subTotal += item.getProductPrice();
        double totalCost = subTotal + DELIVERY_FEE;

        binding.txtSubTotal.setText(String.format("LKR %.2f", subTotal));
        binding.txtDeliveryFee.setText(String.format("LKR %.2f", DELIVERY_FEE));
        binding.txtTotalCost.setText(String.format("LKR %.2f", totalCost));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float x = event.values[0];
                float y = event.values[1];
                float float_z = event.values[2];

                float speed = Math.abs(x + y + float_z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD && !isDialogShowing && cartItemList != null && !cartItemList.isEmpty()) {
                    showClearCartDialog();
                }

                last_x = x;
                last_y = y;
                last_z = float_z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void showClearCartDialog() {
        isDialogShowing = true;
        new AlertDialog.Builder(getContext())
                .setTitle("Clear Cart?")
                .setMessage("Are you sure you want to clear all items in your cart?")
                .setPositiveButton("Yes, Clear All", (dialog, which) -> {
                    clearEntireCart();
                    isDialogShowing = false;
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    isDialogShowing = false;
                })
                .setCancelable(false)
                .show();
    }

    private void clearEntireCart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("users").document(uid).collection("cart").get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            WriteBatch batch = db.batch();
                            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                                batch.delete(doc.getReference());
                            }
                            batch.commit().addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Cart Cleared!", Toast.LENGTH_SHORT).show();
                                cartItemList.clear();
                                if(adapter != null) adapter.notifyDataSetChanged();
                                calculateTotal();
                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to clear cart", Toast.LENGTH_SHORT).show());
                        }
                    });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottomNavView).setVisibility(View.GONE);
        }
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottomNavView).setVisibility(View.VISIBLE);
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }
}