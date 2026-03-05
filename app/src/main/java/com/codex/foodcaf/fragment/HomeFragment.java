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

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.PopularSectionAdapter;
import com.codex.foodcaf.databinding.FragmentHomeBinding;
import com.codex.foodcaf.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class HomeFragment extends Fragment {


    private FragmentHomeBinding binding;


    public HomeFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater,container,false);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadPopularProducts();
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

                            GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);


//                            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                            binding.HomePopSection.itemSectionRecycler.setLayoutManager(layoutManager);

                            PopularSectionAdapter adapter = new PopularSectionAdapter(popularList, product -> {


                            });

                            binding.HomePopSection.itemSectionRecycler.setAdapter(adapter);
                        }
                    }
                });

    }
}