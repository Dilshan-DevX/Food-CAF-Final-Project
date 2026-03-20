package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.FavAdapter;
import com.codex.foodcaf.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavouritesFragment extends Fragment {

    private RecyclerView rvFavourites;
    private LinearLayout emptyState;
    private TextView tvFavCount;
    private FavAdapter adapter;
    private List<Product> favProductList = new ArrayList<>();
    private FirebaseFirestore db;
    private String uid;

    public FavouritesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavourites = view.findViewById(R.id.rvFavourites);
        emptyState = view.findViewById(R.id.emptyState);
        tvFavCount = view.findViewById(R.id.tvFavCount);

        rvFavourites.setLayoutManager(new GridLayoutManager(getContext(), 2));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
            loadFavourites();
        } else {
            showEmptyState();
        }

        getActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = getActivity().findViewById(R.id.bottomNavView);

                if (bottomNav != null) {

                    bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                } else {
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, new HomeFragment())
                            .commit();
                }
            }
        });

    }

    private void loadFavourites() {

        db.collection("users").document(uid).collection("fav").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    List<String> favIds = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        favIds.add(doc.getString("productId"));
                    }

                    if (favIds.isEmpty()) {
                        showEmptyState();
                    } else {
                        db.collection("products").get().addOnSuccessListener(productSnaps -> {
                            favProductList.clear();
                            for (DocumentSnapshot doc : productSnaps) {
                                Product product = doc.toObject(Product.class);
                                if (product != null && favIds.contains(product.getProductId())) {
                                    favProductList.add(product);
                                }
                            }

                            setupAdapter();
                            hideEmptyState(favProductList.size());
                        });
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to load favourites", Toast.LENGTH_SHORT).show());
    }

    private void setupAdapter() {
        adapter = new FavAdapter(favProductList, new FavAdapter.OnFavClickListener() {
            @Override
            public void onViewClick(Product product) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", product.getProductId());
                SingleProductFragment fragment = new SingleProductFragment();
                fragment.setArguments(bundle);

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragment)
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onRemoveClick(Product product, int position) {

                db.collection("users").document(uid).collection("fav")
                        .document(product.getProductId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            favProductList.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, favProductList.size());

                            tvFavCount.setText(favProductList.size() + " saved items");
                            if (favProductList.isEmpty()) {
                                showEmptyState();
                            }
                            Toast.makeText(getContext(), "Removed from favourites", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        rvFavourites.setAdapter(adapter);
    }

    private void showEmptyState() {
        emptyState.setVisibility(View.VISIBLE);
        rvFavourites.setVisibility(View.GONE);
        tvFavCount.setText("0 saved items");
    }

    private void hideEmptyState(int count) {
        emptyState.setVisibility(View.GONE);
        rvFavourites.setVisibility(View.VISIBLE);
        tvFavCount.setText(count + " saved items");
    }
}