package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.FragmentSingleProductBinding;

public class SingleProductFragment extends Fragment {

    FragmentSingleProductBinding binding;

    private String productId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           productId = getArguments().getString("productId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       binding = FragmentSingleProductBinding.inflate(inflater,container,false);
       return binding.getRoot();
    }
}