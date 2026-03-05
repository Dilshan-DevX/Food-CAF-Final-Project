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
import com.codex.foodcaf.model.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SingleProductFragment extends Fragment {

    FragmentSingleProductBinding binding;

    private int quantity = 1;
    private double basePrice = 0.0;
    private String productId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           productId = getArguments().getString("productId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        /// ///////////
//        binding.rvPopularNow.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
        /// ///////////


        /// load data
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("products")
                .whereEqualTo("productId",productId)
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

                           /// //////////////////////////////////////////////////////////////////////////////////////////

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

                            ///////////////////////////////////////////////////////////////////////////////////////////////

//                            binding.txtPrice.setText("LKR "+product.getProductPrice());
                            binding.txtDescription.setText(product.getFoodDetail());
                            binding.txtRate.setText(product.getFoodRating());
                            binding.time.setText(product.getFoodTime());
                            binding.time.setText(product.getFoodTime());

                            if (product.getAttribute() != null){

                                product.getAttribute().forEach(attribute -> {
                                    reanderAttribute(attribute,binding.productAtt);
                                });
                     ///  ///////////////////////////
                                if (product.getCategoryId() != null) {
//                                    loadPopularProducts(product.getCategoryId(), product.getProductId());
                                }
                     /// ///////////////////////////////
                            }


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
//        binding.populerSection.

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

                            PopularSectionAdapter adapter = new PopularSectionAdapter(popularList,product -> {


                            });

                            binding.populerSection.itemSectionRecycler.setAdapter(adapter);
                        }
                    }
                });

    }
    /// //////////////////////////////////////////////////////
//    private void loadPopularProducts(String categoryId, String currentProductId) {
//        FirebaseFirestore.getInstance().collection("products")
//                .whereEqualTo("categoryId", categoryId)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//
//                    List<Product> popularList = new java.util.ArrayList<>();
//
//                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
//                        Product p = doc.toObject(Product.class);
//
//                        if (p != null && p.getProductId() != null && !p.getProductId().equals(currentProductId)) {
//                            popularList.add(p);
//                        }
//                    }
//
//                    com.codex.foodcaf.adapter.PopularAdapter popularAdapter = new com.codex.foodcaf.adapter.PopularAdapter(popularList, selectedProduct -> {
//
//
//                        Bundle bundle = new Bundle();
//                        bundle.putString("productId", selectedProduct.getProductId());
//
//                        SingleProductFragment fragment = new SingleProductFragment();
//                        fragment.setArguments(bundle);
//
//                        getParentFragmentManager().beginTransaction()
//                                .replace(R.id.fragmentContainer, fragment)
//                                .addToBackStack(null)
//                                .commit();
//                    });
//
//                    binding.rvPopularNow.setAdapter(popularAdapter);
//                });
//    }
   /// /////////////////////////////////////////////////////////////////////////////////
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

        String titleName = attribute.getName() != null ? attribute.getName() : "Portion";
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
        for (int i = 0; i < options.size(); i++) {
            String optionName = options.get(i);

            com.google.android.material.button.MaterialButton button = new com.google.android.material.button.MaterialButton(requireContext(), null, com.google.android.material.R.attr.materialButtonOutlinedStyle);
            button.setId(View.generateViewId());
            button.setText(optionName);
            button.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.md_theme_Text2));
            button.setStrokeColor(android.content.res.ColorStateList.valueOf(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.btn_color_primary2)));

            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            button.setLayoutParams(btnParams);

            toggleGroup.addView(button);


            if (i == 0) {
                toggleGroup.check(button.getId());
            }
        }

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