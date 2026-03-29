package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.HomeCategoryAdapter;
import com.codex.foodcaf.adapter.HomeProductAdapter;
import com.codex.foodcaf.databinding.FragmentHomeBinding;
import com.codex.foodcaf.model.Banner;
import com.codex.foodcaf.model.Category;
import com.codex.foodcaf.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private String searchQuery = null;

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            searchQuery = getArguments().getString("SEARCH_QUERY");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (binding != null) {
            binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvCategories.setAdapter(new HomeCategoryAdapter(new java.util.ArrayList<>(), getContext()));

            binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
            binding.rvProducts.setAdapter(new HomeProductAdapter(new java.util.ArrayList<>(), product -> {}));
        }

        loadCategories();
        loadProducts();
        loadBannerFromFirebase();
    }

    private void loadBannerFromFirebase() {
        db.collection("banner")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (binding == null || !isAdded()) return;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Banner bannerData = document.toObject(Banner.class);
                            if (bannerData == null) continue;

                            binding.bannerTitle.setText(
                                    bannerData.getBanner_title() != null
                                            ? bannerData.getBanner_title() : "");

                            binding.banerBody.setText(
                                    bannerData.getBanner_body() != null
                                            ? bannerData.getBanner_body() : "");

                            binding.bannerDate.setText(
                                    bannerData.getBanner_date() != null
                                            ? bannerData.getBanner_date() : "");

                            if (bannerData.getBanner_url() != null
                                    && !bannerData.getBanner_url().isEmpty()) {
                                Glide.with(requireContext())
                                        .load(bannerData.getBanner_url())
                                        .centerCrop()
                                        .placeholder(R.drawable.applogo)
                                        .error(R.drawable.applogo)
                                        .into(binding.imgBanner);
                            }
                        }
                    } else {
                        binding.bannerTitle.setText("Welcome to FoodCaF");
                        binding.banerBody.setText("");
                        binding.bannerDate.setText("");
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding == null || !isAdded()) return;
                    Toast.makeText(getContext(),
                            "Failed to load banner: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCategories() {
        db.collection("categories").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (binding == null || !isAdded()) return;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Category> categoryList = queryDocumentSnapshots.toObjects(Category.class);
                        binding.rvCategories.setLayoutManager(
                                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                        HomeCategoryAdapter categoryAdapter = new HomeCategoryAdapter(categoryList, getContext());
                        binding.rvCategories.setAdapter(categoryAdapter);
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding == null || !isAdded()) return;
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                });
    }


    private void loadProducts() {
        db.collection("products").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (binding == null || !isAdded()) return;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);

                        if (searchQuery != null && !searchQuery.isEmpty()) {
                            Product matchedProduct = null;
                            for (int i = 0; i < productList.size(); i++) {
                                if (productList.get(i).getFoodTitle() != null &&
                                        productList.get(i).getFoodTitle().equalsIgnoreCase(searchQuery)) {
                                    matchedProduct = productList.get(i);
                                    productList.remove(i);
                                    break;
                                }
                            }
                            if (matchedProduct != null) {
                                productList.add(0, matchedProduct);
                            }
                        }

                        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

                        HomeProductAdapter productAdapter = new HomeProductAdapter(productList, product -> {
                            if (binding == null || !isAdded()) return;

                            Bundle bundle = new Bundle();
                            bundle.putString("productId", product.getProductId());

                            SingleProductFragment fragment = new SingleProductFragment();
                            fragment.setArguments(bundle);

                            requireActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragmentContainer, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });

                        binding.rvProducts.setAdapter(productAdapter);

                        if (searchQuery != null && !searchQuery.isEmpty()) {
                            binding.nestedScrollView.post(() -> {
                                binding.nestedScrollView.smoothScrollTo(0, binding.rvProducts.getTop() - 100);
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (binding == null || !isAdded()) return;
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottomNavView);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }

        loadCategories();
        loadProducts();
        loadBannerFromFirebase();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}