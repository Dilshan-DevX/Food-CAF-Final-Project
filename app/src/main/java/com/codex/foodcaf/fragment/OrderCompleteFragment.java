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
import com.codex.foodcaf.databinding.FragmentOrderCompleteBinding;
import com.codex.foodcaf.model.Order;
import com.google.firebase.firestore.FirebaseFirestore;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new OrderFragment())
                        .commit();
            }
        });

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
            startCountdown(25); // Order ID එක නැත්නම් Default විනාඩි 25යි
        }

        // ආපු ගමන් Step 1 Green කරනවා
        setStepActive(binding.iconStep1);

        // තත්පර 30න් Step 2 Green කරනවා
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isAdded() && binding != null) {
                setStepActive(binding.iconStep2);
            }
        }, 30000);

        binding.btncall.setOnClickListener(v -> {
            String phoneNumber = "0725245454"; // ඔයාට ඕන නම්බර් එක මෙතන දෙන්න
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });

        binding.btnMessage.setOnClickListener(v -> {
            String phoneNumber = "0725245454"; // අර කලින් දුන්න නම්බර් එකම දෙන්න

            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + phoneNumber));

             intent.putExtra("sms_body", "Hi, I want to know about my order: #" + orderId);

            startActivity(intent);
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
                                // 1. Date එක සෙට් කිරීම
                                if (order.getOrderDate() != null) {
                                    binding.tvStep1Sub.setText("🕒 " + order.getOrderDate());
                                }

                                // 2. කෑම ගාණ බලලා වෙලාව තීරණය කිරීම
                                List<Order.OrderItem> items = order.getOrderItems();

                                if (items != null && items.size() > 1) {
                                    // කෑම වර්ග එකකට වඩා තියෙනවා නම් කෙළින්ම විනාඩි 25යි
                                    startCountdown(25);
                                } else if (items != null && items.size() == 1) {
                                    // කෑම තියෙන්නේ එකයි නම්, ඒකේ වෙලාව Database එකෙන් ගන්නවා
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

    // එක කෑමක් විතරක් තියෙද්දී ඒකේ වෙලාව ගන්න මෙතඩ් එක
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
                            startCountdown(time); // අදාළ කෑමේ වෙලාවෙන් Timer එක දුවනවා
                        } else {
                            startCountdown(25);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        startCountdown(25);
                    }
                }).addOnFailureListener(e -> {
                    startCountdown(25);
                });
    }

    // අකුරු අයින් කරලා ඉලක්කම ගන්න මෙතඩ් එක
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
        if (countDownTimer != null) {
            countDownTimer.cancel(); // කලින් එකක් දුවනවා නම් නවත්වනවා
        }

        long millis = minutes * 60 * 1000L;

        countDownTimer = new CountDownTimer(millis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (!isAdded() || binding == null) return;

                long minutesLeft = millisUntilFinished / 60000;
                binding.tvETA.setText(String.format("%02d min", minutesLeft));

                // අන්තිම විනාඩියට ආවම Step 3 Green කරනවා
                if (millisUntilFinished <= 60000) {
                    setStepActive(binding.iconStep3);
                }
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
        if (countDownTimer != null) {
            countDownTimer.cancel(); // පිටුවෙන් යද්දී Timer එක නවත්වනවා (Crash නොවෙන්න)
        }
        binding = null;
    }
}