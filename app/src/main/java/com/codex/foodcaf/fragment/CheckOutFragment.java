
package com.codex.foodcaf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.FragmentCheckOutBinding;
import com.codex.foodcaf.model.CartItem;
import com.codex.foodcaf.model.Order;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.StatusResponse;

public class CheckOutFragment extends Fragment implements OnMapReadyCallback {

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
    private String selectedPaymentMethod;
    private int selectedPaymentId;

    private List<CartItem> buyNowItems = null;
    private boolean isBuyNow = false;

    private GoogleMap mMap;
    private static final int LOCATION_REQ_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            isBuyNow = getArguments().getBoolean("IS_BUY_NOW", false);
            if (isBuyNow) {
                Serializable serializable = getArguments().getSerializable("BUY_NOW_ITEMS");
                if (serializable instanceof ArrayList) {
                    //noinspection unchecked
                    buyNowItems = (ArrayList<CartItem>) serializable;
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCheckOutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        com.google.android.gms.maps.SupportMapFragment mapFragment =
                (com.google.android.gms.maps.SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        binding.txtAddressDetail.setEnabled(true);

        binding.btnEditAddress.setOnClickListener(v -> {
            fetchAndSetCurrentLocation(binding.txtAddressDetail);
        });

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
        final double DELIVERY_FEE = 100.00;
        binding.orderItemsContainer.removeAllViews();

        if (isBuyNow && buyNowItems != null && !buyNowItems.isEmpty()) {
            loadOrderSummaryFromItems(buyNowItems, DELIVERY_FEE);
        } else {
            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<CartItem> cartItems = queryDocumentSnapshots.toObjects(CartItem.class);
                            loadOrderSummaryFromItems(cartItems, DELIVERY_FEE);
                        }
                    });
        }

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



            binding.btnConfirmOrder.setEnabled(false);
            binding.btnConfirmOrder.setText("Processing...");

            selectedPaymentId = binding.radioGroupPayment.getCheckedRadioButtonId();
            if (selectedPaymentId == binding.radioCOD.getId()) {
                selectedPaymentMethod = binding.radioCOD.getText().toString();
            } else if (selectedPaymentId == binding.radioCard.getId()) {
                selectedPaymentMethod = binding.radioCard.getText().toString();
            }

            uniqueOrderId = "ORD_" + System.currentTimeMillis();

            if (selectedPaymentId == binding.radioCard.getId()) {
                if (paymentActive) {
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
            } else {
                if (isBuyNow && buyNowItems != null) {
                    placeOrder(buyNowItems, "Pending", null);
                } else {
                    db.collection("users").document(uid).collection("cart")
                            .get()
                            .addOnSuccessListener(qds -> {
                                if (!qds.isEmpty()) {
                                    List<CartItem> cartItems = qds.toObjects(CartItem.class);
                                    placeOrder(cartItems, "Pending", qds);
                                } else {

                                    binding.btnConfirmOrder.setEnabled(true);
                                    binding.btnConfirmOrder.setText("Confirm Order");
                                }
                            })
                            .addOnFailureListener(e -> {
                                binding.btnConfirmOrder.setEnabled(true);
                                binding.btnConfirmOrder.setText("Confirm Order");
                            });
                }
            }

        });
    }

    // Current Location
    private void fetchAndSetCurrentLocation(android.widget.EditText etLocation) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
            return;
        }

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(requireContext(), "Please turn on your Location/GPS", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        Toast.makeText(requireContext(), "Fetching location...", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        if (mMap != null) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("Delivery Location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        }

                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String fullAddress = address.getAddressLine(0);
                                if (etLocation != null) etLocation.setText(fullAddress);
                            } else {
                                Toast.makeText(requireContext(), "Location details not found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Network error, couldn't fetch address", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Please try again later", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (binding.txtAddressDetail != null) {
                    fetchAndSetCurrentLocation(binding.txtAddressDetail);
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void loadOrderSummaryFromItems(List<CartItem> cartItems, double deliveryFee) {
        double subTotal = 0;

        for (CartItem item : cartItems) {
            android.widget.LinearLayout itemLayout = new android.widget.LinearLayout(getContext());
            itemLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            itemLayout.setOrientation(android.widget.LinearLayout.HORIZONTAL);
            itemLayout.setPadding(0, 0, 0, 16);

            android.widget.TextView nameView = new android.widget.TextView(getContext());
            android.widget.LinearLayout.LayoutParams nameParams = new android.widget.LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            nameView.setLayoutParams(nameParams);
            nameView.setText(item.getQty() + " x " + item.getProductName());
            nameView.setTextColor(android.graphics.Color.parseColor("#444444"));
            nameView.setTextSize(14f);

            android.widget.TextView priceView = new android.widget.TextView(getContext());
            priceView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            priceView.setText(String.format("LKR %.2f", item.getProductPrice()));
            priceView.setTextColor(android.graphics.Color.parseColor("#06923E"));
            priceView.setTextSize(14f);
            priceView.setTypeface(null, android.graphics.Typeface.BOLD);

            itemLayout.addView(nameView);
            itemLayout.addView(priceView);
            binding.orderItemsContainer.addView(itemLayout);

            subTotal += item.getProductPrice();
        }

        totalCost = subTotal + deliveryFee;
        binding.txtCheckoutSubtotal.setText(String.format("LKR %.2f", subTotal));
        binding.txtCheckoutDeliveryFee.setText(String.format("LKR %.2f", deliveryFee));
        binding.txtCheckoutTotal.setText(String.format("LKR %.2f", totalCost));
        paymentActive = true;
    }

    private void placeOrder(List<CartItem> cartItems, String status, QuerySnapshot qds) {
        String uid = firebaseAuth.getCurrentUser().getUid();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date());

        Order.Address deliveryDetails = Order.Address.builder()
                .address(newAddress)
                .name(newName)
                .email(newEmail)
                .contactNum(newNumber)
                .build();

        Order order = new Order();
        order.setOrderId(uniqueOrderId);
        order.setUserId(uid);
        order.setDeliveryAddress(deliveryDetails);
        order.setPaymentMethod(selectedPaymentMethod);
        order.setStatus(status);
        order.setOrderDate(formattedDate);

        List<Order.OrderItem> orderItemsList = new ArrayList<>();
        for (CartItem item : cartItems) {
            List<Order.OrderItem.Attribute> orderAttributes = new ArrayList<>();
            if (item.getAttributes() != null) {
                for (CartItem.Attribute attribute : item.getAttributes()) {
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
                .addOnSuccessListener(aVoid -> {
                    if (!isAdded()) return;

                    Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

                    if (qds != null && !qds.isEmpty()) {
                        WriteBatch batch = db.batch();
                        for (DocumentSnapshot document : qds.getDocuments()) {
                            batch.delete(document.getReference());
                        }
                        batch.commit()
                                .addOnSuccessListener(aVoid1 -> navigateToOrderComplete(order.getOrderId()))
                                .addOnFailureListener(e -> navigateToOrderComplete(order.getOrderId()));
                    } else {
                        navigateToOrderComplete(order.getOrderId());
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    binding.btnConfirmOrder.setEnabled(true);
                    binding.btnConfirmOrder.setText("Confirm Order");
                });
    }

//    private void navigateToOrderComplete(String orderId) {
//        if (!isAdded()) return;
//
//        OrderCompleteFragment fragment = new OrderCompleteFragment();
//        Bundle bundle = new Bundle();
//        bundle.putString("ORDER_ID", orderId);
//        fragment.setArguments(bundle);
//
//        requireActivity().getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragmentContainer, fragment)
//                .commitAllowingStateLoss();
//    }
  private void navigateToOrderComplete(String orderId) {
    if (!isAdded()) return;

    View bottomNavView = requireActivity().findViewById(R.id.bottomNavView);
    if (bottomNavView != null) {
        bottomNavView.setVisibility(View.VISIBLE);
    }

    requireActivity().getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

    requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, new OrderFragment())
            .commitAllowingStateLoss();

    if (bottomNavView instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
        ((com.google.android.material.bottomnavigation.BottomNavigationView) bottomNavView).setSelectedItemId(R.id.bottom_order);
    }
}
    private final ActivityResultLauncher<Intent> paymentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    if (data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
                        PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
                        if (response != null && response.isSuccess()) {
                            saveOrderAfterCardPayment(response.getData());
                        } else {
                            Log.e("PAYHERE", response.getData().getMessage());
                            binding.btnConfirmOrder.setEnabled(true);
                            binding.btnConfirmOrder.setText("Confirm Order");
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Log.e("PAYHERE", "Payment Canceled");
                    binding.btnConfirmOrder.setEnabled(true);
                    binding.btnConfirmOrder.setText("Confirm Order");
                }
            });

    private void saveOrderAfterCardPayment(StatusResponse statusResponse) {
        if (isBuyNow && buyNowItems != null) {
            placeOrder(buyNowItems, "Paid", null);
        } else {
            String uid = firebaseAuth.getCurrentUser().getUid();
            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(qds -> {
                        if (!qds.isEmpty()) {
                            List<CartItem> cartItems = qds.toObjects(CartItem.class);
                            placeOrder(cartItems, "Paid", qds);
                        }
                    });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottomNavView).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().findViewById(R.id.bottomNavView).setVisibility(View.GONE);
        }
    }
}