package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codex.foodcaf.R;
import com.codex.foodcaf.adapter.CartAdapter;
import com.codex.foodcaf.databinding.FragmentCartBinding;
import com.codex.foodcaf.model.CartItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;


public class CartFragment extends Fragment {

    private FragmentCartBinding binding;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            String uid = firebaseAuth.getCurrentUser().getUid();

            db.collection("users").document(uid).collection("cart")
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                List<CartItem> cartItems = queryDocumentSnapshots.toObjects(CartItem.class);

                                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                                binding.cartRecyclerView.setLayoutManager(layoutManager);

                                CartAdapter adapter = new CartAdapter(cartItems, product -> {




                                 });
                                binding.cartRecyclerView.setAdapter(adapter);



                            }



                        }
                    });
        }


    }
}