////package com.codex.foodcaf.fragment;
////
////import android.os.Bundle;
////
////import androidx.annotation.NonNull;
////import androidx.annotation.Nullable;
////import androidx.fragment.app.Fragment;
////import androidx.recyclerview.widget.GridLayoutManager;
////import androidx.recyclerview.widget.LinearLayoutManager;
////
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.Toast;
////
////import com.codex.foodcaf.R;
////import com.codex.foodcaf.adapter.HomeCategoryAdapter;
////import com.codex.foodcaf.adapter.HomeProductAdapter;
////import com.codex.foodcaf.databinding.FragmentHomeBinding;
////import com.codex.foodcaf.model.Category;
////import com.codex.foodcaf.model.Product;
////import com.google.firebase.firestore.FirebaseFirestore;
////import com.google.firebase.firestore.QuerySnapshot;
////
////import java.util.List;
////
////public class HomeFragment extends Fragment {
////
////    private FragmentHomeBinding binding;
////    private FirebaseFirestore db;
////
////    public HomeFragment() {
////        // Required empty public constructor
////    }
////
////    @Override
////    public void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        db = FirebaseFirestore.getInstance();
////    }
////
////    @Override
////    public View onCreateView(LayoutInflater inflater, ViewGroup container,
////                             Bundle savedInstanceState) {
////        binding = FragmentHomeBinding.inflate(inflater, container, false);
////        return binding.getRoot();
////    }
////
////    @Override
////    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
////        super.onViewCreated(view, savedInstanceState);
////
////        // Category data load cheyadaniki function call
////        loadCategories();
////
////        // Product data load cheyadaniki function call
////        loadProducts();
////    }
////
////    private void loadCategories() {
////        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
////            if (!queryDocumentSnapshots.isEmpty()) {
////                List<Category> categoryList = queryDocumentSnapshots.toObjects(Category.class);
////
////                // Horizontal list kosam LinearLayoutManager vaadam
////                binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
////                HomeCategoryAdapter categoryAdapter = new HomeCategoryAdapter(categoryList, getContext());
////                binding.rvCategories.setAdapter(categoryAdapter);
////            }
////        }).addOnFailureListener(e -> {
////            Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
////        });
////    }
////
////    private void loadProducts() {
////        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
////            if (!queryDocumentSnapshots.isEmpty()) {
////                List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
////
////                binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
////
////                // 🔴 මෙතනදී තමයි Click Listener එක පාස් කරන්නේ
////                HomeProductAdapter productAdapter = new HomeProductAdapter(productList, product -> {
////
////                    // කෑමක් ක්ලික් කළාම SingleProductFragment එකට යනවා
////                    Bundle bundle = new Bundle();
////                    bundle.putString("productId", product.getProductId());
////
////                    SingleProductFragment fragment = new SingleProductFragment();
////                    fragment.setArguments(bundle);
////
////                    requireActivity().getSupportFragmentManager().beginTransaction()
////                            .replace(R.id.fragmentContainer, fragment)
////                            .addToBackStack(null)
////                            .commit();
////                });
////
////                binding.rvProducts.setAdapter(productAdapter);
////            }
////        }).addOnFailureListener(e -> {
////            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
////        });
////    }
////
////    @Override
////    public void onDestroyView() {
////        super.onDestroyView();
////        binding = null;
////    }
////}
//
//package com.codex.foodcaf.fragment;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.codex.foodcaf.R;
//import com.codex.foodcaf.adapter.HomeCategoryAdapter;
//import com.codex.foodcaf.adapter.HomeProductAdapter;
//import com.codex.foodcaf.databinding.FragmentHomeBinding;
//import com.codex.foodcaf.model.Banner;
//import com.codex.foodcaf.model.Category;
//import com.codex.foodcaf.model.Product;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private FragmentHomeBinding binding;
//    private FirebaseFirestore db;
//
//    public HomeFragment() {}
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        db = FirebaseFirestore.getInstance();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        loadCategories();
//        loadProducts();
//        loadBannerFromFirebase();
//    }
///// ////////////////////////////////////////////////////////////////////
//    private void loadBannerFromFirebase() {
//
//        db.collection("banners")
//                .limit(1)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//
//                    if (!isAdded()) return;
//
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//
//                            Banner bannerData = document.toObject(Banner.class);
//
//                            if (bannerData != null) {
//
//                                binding.bannerTitle.setText(bannerData.getBanner_title());
//
//                                if (bannerData.getBanner_url() != null && !bannerData.getBanner_url().isEmpty()) {
//                                    Glide.with(requireContext())
//                                            .load(bannerData.getBanner_url())
//                                            .centerCrop()
//                                            .placeholder(android.R.drawable.ic_menu_gallery)
//                                            .error(android.R.drawable.stat_notify_error)
//                                            .into(binding.imgBanner); //ImageView එක
//                                }
//                            }
//                        }
//                    } else {
//
//                        binding.bannerTitle.setText("Welcome to FoodCaF");
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    if (isAdded()) {
//                        Toast.makeText(getContext(), "Failed to load banner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }
///// //////////////////////////////////////////////////////////////////////////////////////////////////////
//    private void loadCategories() {
//        db.collection("categories").get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    // ✅ binding null check — fragment detach වුණොත් crash නවත්වනවා
//                    if (binding == null || !isAdded()) return;
//
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        List<Category> categoryList = queryDocumentSnapshots.toObjects(Category.class);
//                        binding.rvCategories.setLayoutManager(
//                                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//                        HomeCategoryAdapter categoryAdapter = new HomeCategoryAdapter(categoryList, getContext());
//                        binding.rvCategories.setAdapter(categoryAdapter);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // ✅ binding null check
//                    if (binding == null || !isAdded()) return;
//                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    private void loadProducts() {
//        db.collection("products").get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    // ✅ binding null check — OrderFragment ගෙන් Home ට navigate වෙද්දී crash fix
//                    if (binding == null || !isAdded()) return;
//
//                    if (!queryDocumentSnapshots.isEmpty()) {
//                        List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
//
//                        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
//
//                        HomeProductAdapter productAdapter = new HomeProductAdapter(productList, product -> {
//                            // ✅ binding null check — click callback ඇතුළේත්
//                            if (binding == null || !isAdded()) return;
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString("productId", product.getProductId());
//
//                            SingleProductFragment fragment = new SingleProductFragment();
//                            fragment.setArguments(bundle);
//
//                            requireActivity().getSupportFragmentManager().beginTransaction()
//                                    .replace(R.id.fragmentContainer, fragment)
//                                    .addToBackStack(null)
//                                    .commit();
//                        });
//
//                        binding.rvProducts.setAdapter(productAdapter);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // ✅ binding null check
//                    if (binding == null || !isAdded()) return;
//                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null; // ✅ binding null කිරීම — memory leak නවත්වනවා
//    }
//}


//package com.codex.foodcaf.fragment;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.codex.foodcaf.R;
//import com.codex.foodcaf.adapter.HomeCategoryAdapter;
//import com.codex.foodcaf.adapter.HomeProductAdapter;
//import com.codex.foodcaf.databinding.FragmentHomeBinding;
//import com.codex.foodcaf.model.Category;
//import com.codex.foodcaf.model.Product;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//
//import java.util.List;
//
//public class HomeFragment extends Fragment {
//
//    private FragmentHomeBinding binding;
//    private FirebaseFirestore db;
//
//    public HomeFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        db = FirebaseFirestore.getInstance();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        // Category data load cheyadaniki function call
//        loadCategories();
//
//        // Product data load cheyadaniki function call
//        loadProducts();
//    }
//
//    private void loadCategories() {
//        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            if (!queryDocumentSnapshots.isEmpty()) {
//                List<Category> categoryList = queryDocumentSnapshots.toObjects(Category.class);
//
//                // Horizontal list kosam LinearLayoutManager vaadam
//                binding.rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//                HomeCategoryAdapter categoryAdapter = new HomeCategoryAdapter(categoryList, getContext());
//                binding.rvCategories.setAdapter(categoryAdapter);
//            }
//        }).addOnFailureListener(e -> {
//            Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    private void loadProducts() {
//        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
//            if (!queryDocumentSnapshots.isEmpty()) {
//                List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);
//
//                binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
//
//                // 🔴 මෙතනදී තමයි Click Listener එක පාස් කරන්නේ
//                HomeProductAdapter productAdapter = new HomeProductAdapter(productList, product -> {
//
//                    // කෑමක් ක්ලික් කළාම SingleProductFragment එකට යනවා
//                    Bundle bundle = new Bundle();
//                    bundle.putString("productId", product.getProductId());
//
//                    SingleProductFragment fragment = new SingleProductFragment();
//                    fragment.setArguments(bundle);
//
//                    requireActivity().getSupportFragmentManager().beginTransaction()
//                            .replace(R.id.fragmentContainer, fragment)
//                            .addToBackStack(null)
//                            .commit();
//                });
//
//                binding.rvProducts.setAdapter(productAdapter);
//            }
//        }).addOnFailureListener(e -> {
//            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}

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

    public HomeFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
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

                            // ✅ bannerTitle — banner_title field
                            binding.bannerTitle.setText(
                                    bannerData.getBanner_title() != null
                                            ? bannerData.getBanner_title() : "");

                            // ✅ banerBody — banner_body field (XML typo: baner not banner)
                            binding.banerBody.setText(
                                    bannerData.getBanner_body() != null
                                            ? bannerData.getBanner_body() : "");

                            // ✅ bannerDate — banner_date field
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
                        // ✅ Banners collection empty unoth default text
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
                    // ✅ binding null check — fragment detach වුණොත් crash නවත්වනවා
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
                    // ✅ binding null check
                    if (binding == null || !isAdded()) return;
                    Toast.makeText(getContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProducts() {
        db.collection("products").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // ✅ binding null check — OrderFragment ගෙන් Home ට navigate වෙද්දී crash fix
                    if (binding == null || !isAdded()) return;

                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<Product> productList = queryDocumentSnapshots.toObjects(Product.class);

                        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));

                        HomeProductAdapter productAdapter = new HomeProductAdapter(productList, product -> {
                            // ✅ binding null check — click callback ඇතුළේත්
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
                    }
                })
                .addOnFailureListener(e -> {
                    // ✅ binding null check
                    if (binding == null || !isAdded()) return;
                    Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // ✅ binding null කිරීම — memory leak නවත්වනවා
    }
}