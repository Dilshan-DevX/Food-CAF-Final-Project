package com.codex.foodcaf.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.FragmentCheckOutBinding;
import com.codex.foodcaf.model.CartItem;
import com.codex.foodcaf.model.Order;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CheckOutFragment extends Fragment {

    private FragmentCheckOutBinding binding;

    private String newAddress;
    private String newName;
    private String newEmail;
    private String newNumber;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCheckOutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(requireContext())
                .load(R.drawable.gmap)
                .into(binding.imgMap);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });


        /// /////////////////////////////// Address enable //////////////////////////////////////////////////

        binding.btnEditAddress.setOnClickListener(v -> {
            boolean isEnabled = binding.txtAddressDetail.isEnabled();

            if (!isEnabled) {
                binding.txtAddressDetail.setEnabled(true);
                binding.txtAddressDetail.requestFocus();

                binding.btnEditAddress.setImageResource(android.R.drawable.ic_menu_save);

                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.txtAddressDetail, InputMethodManager.SHOW_IMPLICIT);

            } else {
                binding.txtAddressDetail.setEnabled(false);

                binding.btnEditAddress.setImageResource(android.R.drawable.ic_menu_edit);

                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.txtAddressDetail.getWindowToken(), 0);

                 newAddress = binding.txtAddressDetail.getText().toString();
            }
        });

        /// /////////////////////////////// Details enable //////////////////////////////////////////////////

        binding.btnEditDetails.setOnClickListener(v -> {
            boolean isEnabled = binding.txtName.isEnabled();

            if (!isEnabled) {

                binding.txtName.setEnabled(true);
                binding.txtEmail.setEnabled(true);
                binding.txtConNum.setEnabled(true);

                binding.txtName.requestFocus();

                binding.btnEditDetails.setImageResource(android.R.drawable.ic_menu_save);

                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.txtName, InputMethodManager.SHOW_IMPLICIT);

            } else {
                binding.txtName.setEnabled(false);
                binding.txtEmail.setEnabled(false);
                binding.txtConNum.setEnabled(false);

                binding.btnEditDetails.setImageResource(R.drawable.edit_24px);

                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.txtName.getWindowToken(), 0);

                 newName = binding.txtName.getText().toString();
                 newEmail = binding.txtEmail.getText().toString();
                 newNumber = binding.txtConNum.getText().toString();




            }
        });


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = firebaseAuth.getCurrentUser().getUid();

        /////////////////////////  Confirm Order Btn////////////////////////////////

        binding.btnConfirmOrder.setOnClickListener(v -> {

            db.collection("users").document(uid).collection("cart")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot qds) {

                            if (!qds.isEmpty()) {



                                String newAddress = binding.txtAddressDetail.getText().toString().trim();
                                String newName = binding.txtName.getText().toString().trim();
                                String newEmail = binding.txtEmail.getText().toString().trim();
                                String newNumber = binding.txtConNum.getText().toString().trim();

                                List<CartItem> cartItems = qds.toObjects(CartItem.class);

                                if (newAddress.isEmpty()) {
                                    binding.txtAddressDetail.setError("Please enter your delivery address");
                                    binding.txtAddressDetail.requestFocus();
                                    return;
                                }

                                if (newName.isEmpty()) {
                                    binding.txtName.setError("Please enter your name");
                                    binding.txtName.requestFocus();
                                    return;
                                }

                                if (newEmail.isEmpty()) {
                                    binding.txtEmail.setError("Please enter your email address");
                                    binding.txtEmail.requestFocus();
                                    return;
                                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                                    binding.txtEmail.setError("Please enter a valid email address");
                                    binding.txtEmail.requestFocus();
                                    return;
                                }

                                if (newNumber.isEmpty()) {
                                    binding.txtConNum.setError("Please enter your contact number");
                                    binding.txtConNum.requestFocus();
                                    return;
                                } else if (newNumber.length() < 10) {
                                    binding.txtConNum.setError("Please enter a valid phone number");
                                    binding.txtConNum.requestFocus();
                                    return;
                                }

                                int selectedPaymentId = binding.radioGroupPayment.getCheckedRadioButtonId();
                                String selectedPaymentMethod = "";

                                if (selectedPaymentId == binding.radioCOD.getId()) {
                                    selectedPaymentMethod = binding.radioCOD.getText().toString(); // "Cash on delivery"
                                } else if (selectedPaymentId == binding.radioCard.getId()) {
                                    selectedPaymentMethod = binding.radioCard.getText().toString(); // "Credit Card"
                                }

                                Order.Address Delivarydetails =  Order.Address.builder()
                                        .address(newAddress)
                                        .name(newName)
                                        .email(newEmail)
                                        .contactNum(newNumber).build();

                                Order order = new Order();
                                order.setOrderId(String.valueOf(System.currentTimeMillis()));
                                order.setUserId(uid);
                                order.setDeliveryAddress(Delivarydetails);
                                order.setPaymentMethod(selectedPaymentMethod);
                                ///
                                ///

                                List<Order.OrderItem> orderItemsList = new ArrayList<>();

                                for(CartItem item : cartItems) {

                                    List<Order.OrderItem.Attribute> orderAttributes = new ArrayList<>();

                                    if (item.getAttributes() != null) {
                                        for(CartItem.Attribute attribute : item.getAttributes()) {
                                            Order.OrderItem.Attribute newOrderAttr = Order.OrderItem.Attribute.builder()
                                                    .values(attribute.getValues())
                                                    .price(attribute.getPrice())
                                                    .build();

                                            orderAttributes.add(newOrderAttr);
                                        }
                                    }

                                    Order.OrderItem newOrderItem = Order.OrderItem.builder()
                                            .productId(item.getProductId())
                                            .productName(item.getProductName())
                                            .unitPrice(item.getUnitPrice())
                                            .qty(item.getQty())
                                            .attributes(orderAttributes)
                                            .totalPrice(item.getProductPrice())
                                            .build();

                                    orderItemsList.add(newOrderItem);
                                }

                                order.setOrderItems(orderItemsList);

                                db.collection("orders").document()
                                        .set(order)
                                        .addOnSuccessListener( aVoid->{
                                            Toast.makeText(getContext(), "Order Saveed", Toast.LENGTH_SHORT).show();
                                        });

                            }
                        }
                    });
        });



    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().findViewById(R.id.bottomNavView).setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().findViewById(R.id.bottomNavView).setVisibility(View.GONE);
    }

}