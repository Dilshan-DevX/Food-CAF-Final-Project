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

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.CategoryAdapter;
import com.codex.foodcaf.databinding.FragmentCategoryBinding;
import com.codex.foodcaf.model.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;


public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private CategoryAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.recyclerViewCategoryPage.setLayoutManager(new LinearLayoutManager();

        FirebaseFirestore db =  FirebaseFirestore.getInstance();
//
//        Category c1 = new Category("cat1", "Pizza", "https://picsum.photos/200/300", "Delicious cheesy pizzas and slices");
//        Category c2 = new Category("cat2", "Fast Food", "https://picsum.photos/200/300", "Burgers, French Fries and Submarines");
//        Category c3 = new Category("cat3", "Sri Lankan", "https://picsum.photos/200/300", "Authentic Rice & Curry, Kottu and Hoppers");
//        Category c4 = new Category("cat4", "Chinese", "https://picsum.photos/200/300", "Hot Soups, Noodles and Fried Rice");
//        Category c5 = new Category("cat5", "Desserts", "https://picsum.photos/200/300", "Ice Creams, Brownies and Sweet Cakes");
//        Category c6 = new Category("cat6", "Beverages", "https://picsum.photos/200/300", "Coffee, Iced Tea and Fresh Fruit Juices");
//        Category c7 = new Category("cat7", "Indian", "https://picsum.photos/200/300", "Spicy Biryani, Naan and Tandoori dishes");
//        Category c8 = new Category("cat8", "Bakery", "https://picsum.photos/200/300", "Freshly baked Bread, Pastries and Short eats");
//        Category c9 = new Category("cat9", "Healthy", "https://picsum.photos/200/300", "Fresh green Salads and Diet meals");
//        Category c10 = new Category("cat10", "Seafood", "https://picsum.photos/200/300", "Fresh Prawns, Crabs and Ocean catches");
//
//        List<Category> cats = List.of(c1,c2,c3,c4,c5,c6,c7,c8,c9,c10);
//
//        WriteBatch batch = db.batch();
//
//        for (Category c : cats) {
//            DocumentReference ref = db.collection("categories").document();
//            batch.set(ref, c);
//        }
//
//        batch.commit();

//        db.collection("categories").add()

        db.collection("categories").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      QuerySnapshot result = task.getResult();
                      List<Category> categories =task.getResult().toObjects(Category.class);
                        adapter = new CategoryAdapter(categories, new CategoryAdapter.OnCategoryClickListener() {
                            @Override
                            public void onCategoryClick(Category category) {
                                Toast.makeText(getContext(), category.getCategoryName(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        binding.recyclerViewCategoryPage.setAdapter(adapter);
                    }
                });


    }
}