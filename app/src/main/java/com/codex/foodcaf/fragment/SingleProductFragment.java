package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.PopularSectionAdapter;
import com.codex.foodcaf.adapter.ProductSliderAdapter;
import com.codex.foodcaf.databinding.FragmentSingleProductBinding;
import com.codex.foodcaf.model.CartItem;
import com.codex.foodcaf.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

public class SingleProductFragment extends Fragment {

    FragmentSingleProductBinding binding;

    private int quantity = 1;
    private double basePrice = 0.0;
    private String productId;

    // 🔴 යූසර් තෝරන Attributes (Portion/Size) ටික මතක තියාගන්න හදපු Map එක
    private Map<String, CartItem.Attribute> selectedAttributesMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getString("productId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSingleProductBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().findViewById(R.id.bottomNavView).setVisibility(View.GONE);
        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        // ඩේටාබේස් එකෙන් කෑම එකේ විස්තර ගැනීම
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("products")
                .whereEqualTo("productId", productId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Product product = queryDocumentSnapshots.getDocuments().get(0).toObject(Product.class);

                            ProductSliderAdapter adapter = new ProductSliderAdapter(product.getProductImage());
                            binding.imgProductMain.setAdapter(adapter);

                            binding.dotsIndicator.attachTo(binding.imgProductMain);

                            binding.txtTitle.setText(product.getFoodTitle());
                            binding.txtDescription.setText(product.getFoodDetail());
                            binding.txtRate.setText(product.getFoodRating());
                            binding.time.setText(product.getFoodTime());

                            // Base Price එක ගැනීම
                            basePrice = product.getProductPrice();

                            if (product.isAvailability()) {
                                quantity = 1;
                                binding.txtQty.setText(String.valueOf(quantity));
                                updatePriceView();

                                binding.btnPlus.setEnabled(true);
                                binding.btnMinus.setEnabled(true);
                                binding.btnAddToCart.setEnabled(true);
                                binding.btnBuyNow.setEnabled(true);
                            } else {
                                quantity = 0;
                                binding.txtQty.setText("0");
                                binding.txtPrice.setText("Currently not available");
                                binding.txtPrice.setTextSize(16);
                                binding.txtPrice.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_error));

                                binding.btnPlus.setEnabled(false);
                                binding.btnMinus.setEnabled(false);
                                binding.btnAddToCart.setEnabled(false);
                                binding.btnBuyNow.setEnabled(false);
                            }

                            // Plus සහ Minus බොත්තම්
                            binding.btnPlus.setOnClickListener(v -> {
                                if (product.isAvailability()) {
                                    quantity++;
                                    binding.txtQty.setText(String.valueOf(quantity));
                                    updatePriceView();
                                }
                            });

                            binding.btnMinus.setOnClickListener(v -> {
                                if (product.isAvailability()) {
                                    if (quantity > 1) {
                                        quantity--;
                                        binding.txtQty.setText(String.valueOf(quantity));
                                        updatePriceView();
                                    } else {
                                        Toast.makeText(getContext(), "Minimum quantity is 1", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            // Attributes ටික UI එකට Load කිරීම
                            if (product.getAttribute() != null) {
                                product.getAttribute().forEach(attribute -> {
                                    reanderAttribute(attribute, binding.productAtt);
                                });
                            }

                            // 🟢 "Add to Cart" Button Logic එක 🟢
//                            binding.btnAddToCart.setOnClickListener(v -> {
//                                if(product.isAvailability()) {
//                                    // 1. Map එකේ තියෙන attributes ටික List එකකට හරවනවා
//                                    List<CartItem.Attribute> finalAttributes = new ArrayList<>(selectedAttributesMap.values());
//
//                                    // 2. CartItem ඔබ්ජෙක්ට් එක හදලා දත්ත ටික සෙට් කරනවා
//                                    CartItem cartItem = new CartItem();
//                                    cartItem.setProductId(productId);
//                                    cartItem.setProductName(product.getFoodTitle());
//                                    // Quantity එකත් එක්ක ගුණ වුණු මුළු ගාණ හරි, එකක ගාණ හරි දෙන්න පුළුවන්
//                                    cartItem.setProductPrice(basePrice * quantity);
//                                    cartItem.setAttributes(finalAttributes);
//
//                                    // 3. Test කරන්න Toast එකක් දානවා (ඔයාට මේක පස්සේ DB එකට Save කරන්න පුළුවන්)
//                                    Toast.makeText(getContext(), "Added to Cart! Total: " + (basePrice * quantity), Toast.LENGTH_SHORT).show();
//
//                                    // 🔴 මෙතනින් ඔයාගේ Cart Database එකට Save කරන කෝඩ් එක ලියන්න 🔴
//                                }
//                            });

                            /// //////////////////////"Add to Cart" Button Logic/////////////////////////////

                            binding.btnAddToCart.setOnClickListener(v -> {
                                if (product.isAvailability()) {
                                    // 1. Map එකේ තියෙන attributes ටික List එකකට හරවනවා
                                    List<CartItem.Attribute> finalAttributes = new ArrayList<>(selectedAttributesMap.values());

                                    // 2. CartItem ඔබ්ජෙක්ට් එක හදලා දත්ත ටික සෙට් කරනවා
                                    CartItem cartItem = new CartItem();
                                    cartItem.setProductId(productId);
                                    cartItem.setProductName(product.getFoodTitle());
                                    cartItem.setProductPrice(basePrice * quantity);
                                    cartItem.setAttributes(finalAttributes);

                                    // 🔴 Log එකේ පෙන්නන්න Portion විස්තර ටික එකතු කරගන්නවා 🔴
                                    StringBuilder portions = new StringBuilder();
                                    for (CartItem.Attribute attr : finalAttributes) {
                                        if (attr.getValues() != null && !attr.getValues().isEmpty()) {
                                            portions.append(attr.getPorsion()).append(": ").append(attr.getValues().get(0)).append(" | ");
                                        }
                                    }

                                    // 🔴 Logcat එකේ පෙන්නන මැසේජ් එක 🔴
                                    String logMessage = "Name: " + product.getFoodTitle() +
                                            " | Qty: " + quantity +
                                            " | Portion: [" + portions.toString() + "]" +
                                            " | Total Price: LKR " + (basePrice * quantity);

                                    // "CART_TEST" කියන නමින් Logcat එකට යවනවා
                                    android.util.Log.d("CART_TEST", logMessage);

                                    // 3. Test කරන්න Toast එකක් දානවා
                                    Toast.makeText(getContext(), "Added! Check Logcat (CART_TEST)", Toast.LENGTH_SHORT).show();

                                    // මෙතනින් ඔයාගේ Cart Database එකට Save කරන කෝඩ් එක ලියන්න පුළුවන්
                                }
                            });


                        /// ////////////////////////////////////////////////////////////////////////////

                        }
                    }
                });

        loadPopularProducts();
    }

    private void updatePriceView() {
        double totalPrice = basePrice * quantity;
        binding.txtPrice.setText("LKR " + String.format("%.2f", totalPrice));
        binding.txtPrice.setTextSize(22);
        binding.txtPrice.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_Text2));
    }

    private void loadPopularProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot qd) {
                        if (!qd.isEmpty()) {
                            List<Product> popularList = qd.toObjects(Product.class);

                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.populerSection.itemSectionRecycler.setLayoutManager(layoutManager);

                            PopularSectionAdapter adapter = new PopularSectionAdapter(popularList, product -> {
                                // මෙතනට Popular item click කලාම වෙන දේ ලියන්න
                            });

                            binding.populerSection.itemSectionRecycler.setAdapter(adapter);
                        }
                    }
                });
    }

    // 🔴 Attribute Render කරන මෙතඩ් එක 🔴
    private void reanderAttribute(Product.Attribute attribute, ViewGroup container) {
        if (attribute == null || attribute.getValues() == null || attribute.getValues().isEmpty()) {
            return;
        }

        LinearLayout mainLayout = new LinearLayout(getContext());
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        mainParams.topMargin = dpToPx(15);
        mainLayout.setLayoutParams(mainParams);

        TextView titleText = new TextView(getContext());
        String titleName = attribute.getPorsion() != null ? attribute.getPorsion() : "Portion Size";
        titleText.setText(titleName);
        titleText.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 16);
        titleText.setTypeface(null, android.graphics.Typeface.BOLD);
        titleText.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_Text2));

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        titleParams.bottomMargin = dpToPx(10);
        titleText.setLayoutParams(titleParams);
        mainLayout.addView(titleText);

        com.google.android.material.button.MaterialButtonToggleGroup toggleGroup = new com.google.android.material.button.MaterialButtonToggleGroup(getContext());
        toggleGroup.setSingleSelection(true);
        toggleGroup.setSelectionRequired(true);
        LinearLayout.LayoutParams toggleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(45)
        );
        toggleGroup.setLayoutParams(toggleParams);

        List<String> options = attribute.getValues();
        List<String> optionPricesStr = attribute.getPrice();

        for (int i = 0; i < options.size(); i++) {
            String optionName = options.get(i);

            double optionPrice = 0.0;
            if (optionPricesStr != null && optionPricesStr.size() > i) {
                try {
                    optionPrice = Double.parseDouble(optionPricesStr.get(i));
                } catch (NumberFormatException e) {
                    optionPrice = 0.0;
                }
            }

            com.google.android.material.button.MaterialButton button = new com.google.android.material.button.MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            button.setId(View.generateViewId());
            button.setText(optionName);
            button.setCheckable(true);
            button.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_Text2));
            button.setStrokeColor(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.btn_color_primary2)));

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            button.setLayoutParams(btnParams);
            button.setTag(optionPrice);

            toggleGroup.addView(button);

            // පළවෙනි බට්න් එක ලෝඩ් වෙද්දීම Map එකට Save කිරීම
            if (i == 0) {
                toggleGroup.check(button.getId());
                basePrice = optionPrice;
                updatePriceView();

                // 🟢 Map එකට සේව් කිරීම (Default Selection)
                CartItem.Attribute cartAttr = new CartItem.Attribute();
                cartAttr.setPorsion(attribute.getPorsion());
                cartAttr.setType(attribute.getType());
                cartAttr.setValues(Collections.singletonList(optionName));
                cartAttr.setPrice(Collections.singletonList(String.valueOf(optionPrice)));

                selectedAttributesMap.put(attribute.getPorsion(), cartAttr);
            }
        }

        // යූසර් වෙන බට්න් එකක් එබුවම Map එක Update වීම
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                com.google.android.material.button.MaterialButton selectedButton = group.findViewById(checkedId);
                if (selectedButton != null && selectedButton.getTag() != null) {
                    double newPrice = (Double) selectedButton.getTag();
                    basePrice = newPrice;
                    updatePriceView();

                    // 🟢 Map එක අලුත් අගයන්ගෙන් යාවත්කාලීන කිරීම
                    CartItem.Attribute cartAttr = new CartItem.Attribute();
                    cartAttr.setPorsion(attribute.getPorsion());
                    cartAttr.setType(attribute.getType());
                    cartAttr.setValues(Collections.singletonList(selectedButton.getText().toString()));
                    cartAttr.setPrice(Collections.singletonList(String.valueOf(newPrice)));

                    selectedAttributesMap.put(attribute.getPorsion(), cartAttr);
                }
            }
        });

        mainLayout.addView(toggleGroup);
        container.addView(mainLayout);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
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