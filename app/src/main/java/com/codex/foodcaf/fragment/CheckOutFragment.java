package com.codex.foodcaf.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckOutFragment extends Fragment {

    private FragmentCheckOutBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    private String newAddress;
    private String newName;
    private String newEmail;
    private String newNumber;

    private double totalCost;

    private boolean paymentActive;

    private String uniqueOrderId;

     private  String selectedPaymentMethod;

    private int selectedPaymentId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

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


        String uid = firebaseAuth.getCurrentUser().getUid();

        /////////////////////////   Order Summary Load ////////////////////////////////

        if (firebaseAuth.getCurrentUser() != null) {

            // Delivery ගාස්තුව (ඔයාට ඕන ගාණක් මෙතන දෙන්න)
            final double DELIVERY_FEE = 100.00;

            // මුලින්ම Container එකේ තියෙන පරණ දේවල් මකලා දානවා
            binding.orderItemsContainer.removeAllViews();

            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {

                            List<CartItem> cartItems = queryDocumentSnapshots.toObjects(CartItem.class);
                            double subTotal = 0;

                            // Cart එකේ තියෙන හැම Item එකකටම අලුත් පේළියක් (View එකක්) හදනවා
                            for (CartItem item : cartItems) {

                                // පේළිය හදන LinearLayout එක
                                android.widget.LinearLayout itemLayout = new android.widget.LinearLayout(getContext());
                                itemLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                                itemLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
                                itemLayout.setPadding(0, 0, 0, 16); // යටින් පොඩි ඉඩක් තියනවා

                                // 1. කෑම එකේ නම සහ ප්‍රමාණය (උදා: "2 x Cheese Burger")
                                android.widget.TextView nameView = new android.widget.TextView(getContext());
                                android.widget.LinearLayout.LayoutParams nameParams = new android.widget.LinearLayout.LayoutParams(0, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
                                nameView.setLayoutParams(nameParams);
                                nameView.setText(item.getQty() + " x " + item.getProductName());
                                nameView.setTextColor(android.graphics.Color.parseColor("#444444"));
                                nameView.setTextSize(14f);


                                android.widget.TextView priceView = new android.widget.TextView(getContext());
                                priceView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
                                priceView.setText(String.format("LKR %.2f", item.getProductPrice()));
                                priceView.setTextColor(android.graphics.Color.parseColor("#06923E"));
                                priceView.setTextSize(14f);
                                priceView.setTypeface(null, android.graphics.Typeface.BOLD);

                                itemLayout.addView(nameView);
                                itemLayout.addView(priceView);
                                binding.orderItemsContainer.addView(itemLayout);

                                subTotal += item.getProductPrice();
                            }

                            totalCost = subTotal + DELIVERY_FEE;

                            binding.txtCheckoutSubtotal.setText(String.format("LKR %.2f", subTotal));
                            binding.txtCheckoutDeliveryFee.setText(String.format("LKR %.2f", DELIVERY_FEE));
                            binding.txtCheckoutTotal.setText(String.format("LKR %.2f", totalCost));
                            paymentActive = true;
                        }
                    });
        }


        /////////////////////////  Order Payment ////////////////////////////////

        binding.btnConfirmOrder.setOnClickListener(view1 -> {




            newAddress = binding.txtAddressDetail.getText().toString().trim();
            newName = binding.txtName.getText().toString().trim();
            newEmail = binding.txtEmail.getText().toString().trim();
            newNumber = binding.txtConNum.getText().toString().trim();

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


            selectedPaymentId = binding.radioGroupPayment.getCheckedRadioButtonId();
            if (selectedPaymentId == binding.radioCOD.getId()) {
                selectedPaymentMethod = binding.radioCOD.getText().toString(); // "Cash on delivery"
            } else if (selectedPaymentId == binding.radioCard.getId()) {
                selectedPaymentMethod = binding.radioCard.getText().toString(); // "Credit Card"
            }

            uniqueOrderId = "ORD_" + System.currentTimeMillis();

            if (selectedPaymentId == binding.radioCard.getId()){


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                String formattedDate = sdf.format(new Date());



                if (paymentActive){


                    InitRequest req = new InitRequest();

                    req.setSandBox(true);
                    req.setMerchantId("1225048");
                    req.setMerchantSecret("MTMxNzk2OTI5MDIxNDIxNDMwOTEyOTkzMjkyMjc2MjQwNTU3MzAyOQ==");
                    req.setCurrency("LKR");
                    req.setAmount(totalCost);
                    req.setOrderId(uniqueOrderId);
                    req.setItemsDescription("Thank you for your order");

                    req.getCustomer().setFirstName(newName);
                    req.getCustomer().setLastName(".");
                    req.getCustomer().setEmail(newEmail);
                    req.getCustomer().setPhone(newNumber);
                    req.getCustomer().getAddress().setAddress(newAddress);
                    req.getCustomer().getAddress().setCity(".");
                    req.getCustomer().getAddress().setCountry("Sri Lanka");

                    req.setNotifyUrl("https://foodcaf.requestcatcher.com/");

                    Intent intent = new Intent(getActivity(), PHMainActivity.class);
                    intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

                    paymentLauncher.launch(intent);

                }

            }else {

                db.collection("users").document(uid).collection("cart")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot qds) {

                                if (!qds.isEmpty()) {

                                    List<CartItem> cartItems = qds.toObjects(CartItem.class);

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                    String formattedDate = sdf.format(new Date());

                                    Order.Address Delivarydetails =  Order.Address.builder()
                                            .address(newAddress)
                                            .name(newName)
                                            .email(newEmail)
                                            .contactNum(newNumber).build();

                                    Order order = new Order();
                                    order.setOrderId(uniqueOrderId);
                                    order.setUserId(uid);
                                    order.setDeliveryAddress(Delivarydetails);
                                    order.setPaymentMethod(selectedPaymentMethod);
                                    order.setStatus("Pending");
                                    order.setOrderDate(formattedDate);


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

                                    db.collection("orders").document(order.getOrderId())
                                            .set(order)
                                            .addOnSuccessListener( aVoid->{
                                                Toast.makeText(getContext(), "Order placed Successfully", Toast.LENGTH_SHORT).show();

                                                WriteBatch batch = db.batch();

                                                for (DocumentSnapshot document : qds.getDocuments()) {
                                                    batch.delete(document.getReference());
                                                }
                                                batch.commit().addOnSuccessListener(aVoid1 -> {
                                                    requireActivity().getSupportFragmentManager().popBackStack();

                                                }).addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });

                                            });
                                }
                            }
                        });
            }




        });


        /////////////////////////  Confirm Order Btn ////////////////////////////////

//        binding.btnConfirmOrder.setOnClickListener(v -> {
//
//            db.collection("users").document(uid).collection("cart")
//                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                        @Override
//                        public void onSuccess(QuerySnapshot qds) {
//
//                            if (!qds.isEmpty()) {
//
//                                String newAddress = binding.txtAddressDetail.getText().toString().trim();
//                                String newName = binding.txtName.getText().toString().trim();
//                                String newEmail = binding.txtEmail.getText().toString().trim();
//                                String newNumber = binding.txtConNum.getText().toString().trim();
//
//                                List<CartItem> cartItems = qds.toObjects(CartItem.class);
//
//                                if (newAddress.isEmpty()) {
//                                    binding.txtAddressDetail.setError("Please enter your delivery address");
//                                    binding.txtAddressDetail.requestFocus();
//                                    return;
//                                }
//
//                                if (newName.isEmpty()) {
//                                    binding.txtName.setError("Please enter your name");
//                                    binding.txtName.requestFocus();
//                                    return;
//                                }
//
//                                if (newEmail.isEmpty()) {
//                                    binding.txtEmail.setError("Please enter your email address");
//                                    binding.txtEmail.requestFocus();
//                                    return;
//                                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
//                                    binding.txtEmail.setError("Please enter a valid email address");
//                                    binding.txtEmail.requestFocus();
//                                    return;
//                                }
//
//                                if (newNumber.isEmpty()) {
//                                    binding.txtConNum.setError("Please enter your contact number");
//                                    binding.txtConNum.requestFocus();
//                                    return;
//                                } else if (newNumber.length() < 10) {
//                                    binding.txtConNum.setError("Please enter a valid phone number");
//                                    binding.txtConNum.requestFocus();
//                                    return;
//                                }
//
//                                int selectedPaymentId = binding.radioGroupPayment.getCheckedRadioButtonId();
//                                String selectedPaymentMethod = "";
//
//                                if (selectedPaymentId == binding.radioCOD.getId()) {
//                                    selectedPaymentMethod = binding.radioCOD.getText().toString(); // "Cash on delivery"
//                                } else if (selectedPaymentId == binding.radioCard.getId()) {
//                                    selectedPaymentMethod = binding.radioCard.getText().toString(); // "Credit Card"
//                                }
//                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
//                                String formattedDate = sdf.format(new Date());
//
//                                Order.Address Delivarydetails =  Order.Address.builder()
//                                        .address(newAddress)
//                                        .name(newName)
//                                        .email(newEmail)
//                                        .contactNum(newNumber).build();
//
//                                Order order = new Order();
//                                order.setOrderId(String.valueOf(System.currentTimeMillis()));
//                                order.setUserId(uid);
//                                order.setDeliveryAddress(Delivarydetails);
//                                order.setPaymentMethod(selectedPaymentMethod);
//                                order.setStatus("Pending");
//                                order.setOrderDate(formattedDate);
//
//
//                                List<Order.OrderItem> orderItemsList = new ArrayList<>();
//
//                                for(CartItem item : cartItems) {
//
//                                    List<Order.OrderItem.Attribute> orderAttributes = new ArrayList<>();
//
//                                    if (item.getAttributes() != null) {
//                                        for(CartItem.Attribute attribute : item.getAttributes()) {
//                                            Order.OrderItem.Attribute newOrderAttr = Order.OrderItem.Attribute.builder()
//                                                    .values(attribute.getValues())
//                                                    .price(attribute.getPrice())
//                                                    .build();
//
//                                            orderAttributes.add(newOrderAttr);
//                                        }
//                                    }
//
//                                    Order.OrderItem newOrderItem = Order.OrderItem.builder()
//                                            .productId(item.getProductId())
//                                            .productName(item.getProductName())
//                                            .unitPrice(item.getUnitPrice())
//                                            .qty(item.getQty())
//                                            .attributes(orderAttributes)
//                                            .totalPrice(item.getProductPrice())
//                                            .build();
//
//                                    orderItemsList.add(newOrderItem);
//                                }
//
//                                order.setOrderItems(orderItemsList);
//
//                                db.collection("orders").document()
//                                        .set(order)
//                                        .addOnSuccessListener( aVoid->{
//                                            Toast.makeText(getContext(), "Order Saveed", Toast.LENGTH_SHORT).show();
//                                        });
//
//                            }
//                        }
//                    });
//        });



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

    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
            Intent data = result.getData();

            if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)){
                PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

                if (response != null && response.isSuccess()){
                    StatusResponse statusResponse = response.getData();

                    saveOrderToFirebase(statusResponse);


                }else {
                    Log.e("PAYHERE",response.getData().getMessage());

                }
            }

        }else if (result.getResultCode() == Activity.RESULT_CANCELED){
            Log.e("PAYHERE","Payment Canceled");
        }

    });

    private void saveOrderToFirebase(StatusResponse statusResponse) {


        String uid = firebaseAuth.getCurrentUser().getUid();


                    db.collection("users").document(uid).collection("cart")
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot qds) {

                            if (!qds.isEmpty()) {

                                List<CartItem> cartItems = qds.toObjects(CartItem.class);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
                                String formattedDate = sdf.format(new Date());

                                Order.Address Delivarydetails =  Order.Address.builder()
                                        .address(newAddress)
                                        .name(newName)
                                        .email(newEmail)
                                        .contactNum(newNumber).build();

                                Order order = new Order();
                                order.setOrderId(uniqueOrderId);
                                order.setUserId(uid);
                                order.setDeliveryAddress(Delivarydetails);
                                order.setPaymentMethod(selectedPaymentMethod);
                                order.setStatus("Paid");
                                order.setOrderDate(formattedDate);


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

                                db.collection("orders").document(order.getOrderId())
                                        .set(order)
                                        .addOnSuccessListener( aVoid->{
                                            Toast.makeText(getContext(), "Order Saved Successfully", Toast.LENGTH_SHORT).show();

                                            WriteBatch batch = db.batch();

                                            for (DocumentSnapshot document : qds.getDocuments()) {
                                                batch.delete(document.getReference());
                                            }
                                            batch.commit().addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(getContext(), "Cart Cleared!", Toast.LENGTH_SHORT).show();
                                                requireActivity().getSupportFragmentManager().popBackStack();

                                            }).addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Failed to clear cart: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });

                                        });
                            }
                        }
                    });
    }

}