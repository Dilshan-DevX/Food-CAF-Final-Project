package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.CatListAdapter;
import com.codex.foodcaf.databinding.FragmentCatListBinding;
import com.codex.foodcaf.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class CatListFragment extends Fragment {

    private FragmentCatListBinding binding;

    private CatListAdapter adapter;

    private String categoryId;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCatListBinding.inflate(inflater,container,false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.recyclerViewFood.setLayoutManager(new GridLayoutManager(getContext(), 2));
        FirebaseFirestore db = FirebaseFirestore.getInstance();


//
//        Product p1 = new Product("p1", "cat1", "4.8", "Margherita Pizza", 1500.00, "https://picsum.photos/200/300?random=11", "25m", "Classic cheese and tomato pizza", true);
//        Product p2 = new Product("p2", "cat1", "4.5", "Spicy Chicken Pizza", 2200.00, "https://picsum.photos/200/300?random=12", "30m", "Spicy chicken with jalapeños and extra cheese", true);
//
//// Category 2: Fast Food ("cat2")
//        Product p3 = new Product("p3", "cat2", "4.6", "Cheese Burger", 950.00, "https://picsum.photos/200/300?random=13", "15m", "Beef patty with double cheese and secret sauce", true);
//        Product p4 = new Product("p4", "cat2", "4.2", "French Fries", 450.00, "https://picsum.photos/200/300?random=14", "10m", "Crispy golden fries with ketchup", true);
//
//// Category 3: Sri Lankan ("cat3")
//        Product p5 = new Product("p5", "cat3", "4.9", "Chicken Kottu", 1200.00, "https://picsum.photos/200/300?random=15", "20m", "Spicy Sri Lankan street food with roast chicken", true);
//        Product p6 = new Product("p6", "cat3", "4.7", "Egg Hoppers Set", 350.00, "https://picsum.photos/200/300?random=16", "15m", "5 crispy hoppers with lunu miris", false); // මේක දැනට out of stock (false)
//
//// Category 4: Beverages ("cat4")
//        Product p7 = new Product("p7", "cat4", "4.4", "Iced Latte", 800.00, "https://picsum.photos/200/300?random=17", "5m", "Refreshing iced coffee with milk", true);
//        Product p8 = new Product("p8", "cat4", "4.8", "Fresh Mango Juice", 500.00, "https://picsum.photos/200/300?random=18", "5m", "100% natural fresh mango juice", true);
//;
//
//        List<Product> cats = List.of(p1,p2,p3,p4,p5,p6,p7,p8);
//
//        WriteBatch batch = db.batch();
//
//        for (Product c : cats) {
//            DocumentReference ref = db.collection("products").document();
//            batch.set(ref, c);
//        }
//
//        batch.commit();




        db.collection("products")
                .whereEqualTo("categoryId",categoryId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()){
                            List<Product> products = queryDocumentSnapshots.toObjects(Product.class);

                            adapter = new CatListAdapter(products,product -> {

                            });
                            binding.recyclerViewFood.setAdapter(adapter);
                        }
                    }
                });
    }
}