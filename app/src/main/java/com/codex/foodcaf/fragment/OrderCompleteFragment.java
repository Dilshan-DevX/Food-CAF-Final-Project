package com.codex.foodcaf.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.activity.MainActivity;
import com.codex.foodcaf.databinding.FragmentOrderCompleteBinding;
import com.codex.foodcaf.model.Order;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrderCompleteFragment extends Fragment {

    private FragmentOrderCompleteBinding binding;
    private FirebaseFirestore db;
    private String orderId;
    private CountDownTimer countDownTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderCompleteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();

        Glide.with(requireContext())
                .load(R.drawable.applogo)
                .into(binding.imgDriver);

        if (getArguments() != null) {
            orderId = getArguments().getString("ORDER_ID");
        }

        if (orderId != null) {
            binding.tvInvoice.setText("Invoice : #" + orderId);
            loadOrderDetails();
        } else {
            startCountdown(25);
        }

        setStepActive(binding.iconStep1);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && binding != null) {
                setStepActive(binding.iconStep2);
            }
        }, 30000);

        binding.btncall.setOnClickListener(v -> {
            String phoneNumber = "0725245454";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });

        binding.btnMessage.setOnClickListener(v -> {
            String phoneNumber = "0725245454";
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", "Hi, I want to know about my order: #" + orderId);
            startActivity(intent);
        });


        binding.btnMyOrder.setOnClickListener(v -> {

            View bottomNavView = requireActivity().findViewById(R.id.bottomNavView);
            if (bottomNavView != null) {
                bottomNavView.setVisibility(View.VISIBLE);
            }


            requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);


            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new OrderFragment())
                    .commitAllowingStateLoss();

            if (bottomNavView instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                ((com.google.android.material.bottomnavigation.BottomNavigationView) bottomNavView).getMenu().findItem(R.id.bottom_order).setChecked(true);
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                View bottomNavView = requireActivity().findViewById(R.id.bottomNavView);
                if (bottomNavView != null) {
                    bottomNavView.setVisibility(View.VISIBLE);
                }


                requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commitAllowingStateLoss();

                if (bottomNavView instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                    ((com.google.android.material.bottomnavigation.BottomNavigationView) bottomNavView).getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
                }
            }
        });
    }

    private void loadOrderDetails() {
        db.collection("orders").document(orderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded() || binding == null) return;
                    try {
                        if (documentSnapshot.exists()) {
                            Order order = documentSnapshot.toObject(Order.class);
                            if (order != null) {
                                if (order.getOrderDate() != null) {
                                    binding.tvStep1Sub.setText("🕒 " + order.getOrderDate());
                                }
                                List<Order.OrderItem> items = order.getOrderItems();

                                if (items != null && items.size() > 1) {
                                    getMultipleItemsTime(items);
                                } else if (items != null && items.size() == 1) {
                                    getSingleItemTime(items.get(0).getProductId());
                                } else {
                                    startCountdown(25);
                                }
                            } else {
                                startCountdown(25);
                            }
                        } else {
                            startCountdown(25);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        startCountdown(25);
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && binding != null) startCountdown(25);
                });
    }


    private void getMultipleItemsTime(List<Order.OrderItem> items) {
        List<String> productIds = new ArrayList<>();
        for (Order.OrderItem item : items) {
            if (item.getProductId() != null && !productIds.contains(item.getProductId())) {
                productIds.add(item.getProductId());
            }
        }

        if (productIds.isEmpty()) {
            startCountdown(25);
            return;
        }


        if (productIds.size() > 10) {
            productIds = productIds.subList(0, 10);
        }

        db.collection("products").whereIn("productId", productIds).get()
                .addOnSuccessListener(querySnapshot -> {
                    try {
                        if (!querySnapshot.isEmpty()) {
                            int maxTime = 0;
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                String timeStr = doc.getString("foodTime");
                                int time = extractTime(timeStr);
                                if (time > maxTime) {
                                    maxTime = time; // Set max time
                                }
                            }
                            if (maxTime == 0) maxTime = 25;
                            startCountdown(maxTime);
                        } else {
                            startCountdown(25);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        startCountdown(25);
                    }
                }).addOnFailureListener(e -> startCountdown(25));
    }

    private void getSingleItemTime(String productId) {
        if (productId == null) {
            startCountdown(25);
            return;
        }
        db.collection("products").whereEqualTo("productId", productId).get()
                .addOnSuccessListener(querySnapshot -> {
                    try {
                        if (!querySnapshot.isEmpty()) {
                            String timeStr = querySnapshot.getDocuments().get(0).getString("foodTime");
                            int time = extractTime(timeStr);
                            startCountdown(time);
                        } else {
                            startCountdown(25);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        startCountdown(25);
                    }
                }).addOnFailureListener(e -> startCountdown(25));
    }

    private int extractTime(String timeStr) {
        try {
            if (timeStr == null || timeStr.isEmpty()) return 25;
            String numberOnly = timeStr.replaceAll("[^0-9]", "");
            return Integer.parseInt(numberOnly);
        } catch (Exception e) {
            return 25;
        }
    }

    private void startCountdown(int minutes) {
        if (countDownTimer != null) countDownTimer.cancel();
        long millis = minutes * 60 * 1000L;
        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isAdded() || binding == null) return;
                long minutesLeft = millisUntilFinished / 60000;
                binding.tvETA.setText(String.format("%02d min", minutesLeft));
                if (millisUntilFinished <= 60000) setStepActive(binding.iconStep3);
            }
            @Override
            public void onFinish() {
                if (!isAdded() || binding == null) return;
                binding.tvETA.setText("00 min");
                setStepActive(binding.iconStep3);
            }
        }.start();
    }

    private void setStepActive(ImageView icon) {
        icon.setBackgroundResource(R.drawable.bg_circle_solid_green);
        icon.setColorFilter(Color.WHITE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
        binding = null;
    }
}