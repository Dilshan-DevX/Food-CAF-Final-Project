package com.codex.foodcaf.fragment;

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

import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private CartAdapter adapter;
    private List<CartItem> cartItemList;


    private final double DELIVERY_FEE = 100.00;

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

            // Firebase එකෙන් Cart එකේ දත්ත ගැනීම
            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                cartItemList = queryDocumentSnapshots.toObjects(CartItem.class);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                binding.cartRecyclerView.setLayoutManager(layoutManager);

                                /// ///////////////////////////////////////////////////////////////////////////

                                adapter = new CartAdapter(cartItemList, new CartAdapter.CartItemInteractionListener() {

                                    @Override
                                    public void onQuantityUpdated(CartItem cartItem) {
                                        if (cartItem.getProductId() == null || cartItem.getProductId().isEmpty()) {
                                            return;
                                        }

                                        // 🔴 1. Update කරන්න කලින් Portion එක හොයාගෙන අලුත් Document ID එක හදාගන්නවා
                                        String portion = "Regular";
                                        if (cartItem.getAttributes() != null && !cartItem.getAttributes().isEmpty()) {
                                            portion = cartItem.getAttributes().get(0).getValues().get(0);
                                        }
                                        String docId = cartItem.getProductId() + "_" + portion;

                                        // Firebase database එක update කරනවා
                                        db.collection("users").document(uid).collection("cart")
                                                .document(docId) // 🔴 මෙතන cartItem.getProductId() වෙනුවට docId දෙනවා
                                                .update(
                                                        "qty", cartItem.getQty(),
                                                        "productPrice", cartItem.getProductPrice()
                                                )
                                                .addOnSuccessListener(aVoid -> {
                                                    calculateTotal();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }

                                    @Override
                                    public void onItemRemoved(CartItem cartItem) {
                                        if (cartItem.getProductId() == null || cartItem.getProductId().isEmpty()) {
                                            Toast.makeText(getContext(), "Error: Product ID is missing!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String portion = "Regular";
                                        if (cartItem.getAttributes() != null && !cartItem.getAttributes().isEmpty()) {
                                            portion = cartItem.getAttributes().get(0).getValues().get(0);
                                        }
                                        String docId = cartItem.getProductId() + "_" + portion;

                                        db.collection("users").document(uid).collection("cart")
                                                .document(docId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    Toast.makeText(getContext(), "Item removed successfully!", Toast.LENGTH_SHORT).show();
                                                    calculateTotal();
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Failed to remove: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                });

                            /// /////////////////////////////////////////////////////////////////////////


//                                adapter = new CartAdapter(cartItemList, new CartAdapter.CartItemInteractionListener() {
//
//                                    @Override
//                                    public void onQuantityUpdated(CartItem cartItem) {
//                                        // Product ID eka null da kiyala check karanawa
//                                        if (cartItem.getProductId() == null || cartItem.getProductId().isEmpty()) {
//                                            return;
//                                        }
//
//                                        // Firebase database eke qty ekayi price ekayi update karanawa
//                                        db.collection("users").document(uid).collection("cart")
//                                                .document(cartItem.getProductId())
//                                                .update(
//                                                        "qty", cartItem.getQty(),
//                                                        "productPrice", cartItem.getProductPrice()
//                                                )
//                                                .addOnSuccessListener(aVoid -> {
//                                                    // Database eka update unata passe yatin thiyena total bill eka hadanawa
//                                                    calculateTotal();
//                                                })
//                                                .addOnFailureListener(e -> {
//                                                    Toast.makeText(getContext(), "Failed to update quantity: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                                });
//                                    }
//
//                                    @Override
//                                    public void onItemRemoved(CartItem cartItem) {
//
//                                        // Product ID එක හිස්ද කියලා බලනවා
//                                        if (cartItem.getProductId() == null || cartItem.getProductId().isEmpty()) {
//                                            Toast.makeText(getContext(), "Error: Product ID is missing!", Toast.LENGTH_SHORT).show();
//                                            return;
//                                        }
//
//                                        // Firebase එකෙන් මකනවා
//                                        db.collection("users").document(uid).collection("cart")
//                                                .document(cartItem.getProductId())
//                                                .delete()
//                                                .addOnSuccessListener(aVoid -> {
//                                                    Toast.makeText(getContext(), "Item removed successfully!", Toast.LENGTH_SHORT).show();
//                                                    calculateTotal(); // බිල අලුත් කරනවා
//                                                })
//                                                .addOnFailureListener(e -> {
//                                                    Toast.makeText(getContext(), "Failed to remove: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                                                });
//                                    }
//
//                                    @Override
//                                    public void onItemClick(Product product) {
//                                        // පින්තූරය එබුවම ආයෙත් අදාළ කෑමේ Single Page එකට යනවා
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("productId", product.getProductId());
//
//                                        SingleProductFragment fragment = new SingleProductFragment();
//                                        fragment.setArguments(bundle);
//
//                                        getParentFragmentManager().beginTransaction()
//                                                .replace(R.id.fragmentContainer, fragment)
//                                                .addToBackStack(null)
//                                                .commit();
//                                    }
//                                });

                                binding.cartRecyclerView.setAdapter(adapter);

                                calculateTotal();
                            }
                        }
                    });
        }

        binding.btnCheckout.setOnClickListener(view1 -> {
                    if (cartItemList == null || cartItemList.isEmpty()) {
                        Toast.makeText(getContext(), "Please add items to your cart before checking out.", Toast.LENGTH_SHORT).show();
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
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();
                 BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNavView);
                 bottomNav.setSelectedItemId(R.id.bottom_nav_home);
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

        for (CartItem item : cartItemList) {
            subTotal += item.getProductPrice();
        }

        double totalCost = subTotal + DELIVERY_FEE;


        binding.txtSubTotal.setText(String.format("LKR %.2f", subTotal));
        binding.txtDeliveryFee.setText(String.format("LKR %.2f", DELIVERY_FEE));
        binding.txtTotalCost.setText(String.format("LKR %.2f", totalCost));
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