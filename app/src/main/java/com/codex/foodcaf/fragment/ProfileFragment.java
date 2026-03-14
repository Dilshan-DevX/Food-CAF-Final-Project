package com.codex.foodcaf.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.model.User;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            View toolbar = getActivity().findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setVisibility(View.GONE);
            }
        }

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.bottom_profile).setEnabled(false);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(requireContext(), "Please log in to view profile", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomeFragment())
                    .commit();
            return;
        }


        ShapeableImageView imgProfile = view.findViewById(R.id.imgProfile);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        TextView tvUserEmail = view.findViewById(R.id.tvUserEmail);


        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (isAdded() && documentSnapshot.exists()) {

                        User user = documentSnapshot.toObject(User.class);

                        if (user != null) {

                            tvUserName.setText(user.getName() != null ? user.getName() : "Food Caf User");
                            tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "No Email");


                            String profilePicUrl = user.getProfilePicUrl();
                            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                Glide.with(requireContext())
                                        .load(profilePicUrl)
                                        .centerCrop()
                                        .placeholder(R.drawable.man)
                                        .error(R.drawable.man)
                                        .into(imgProfile);
                            } else {
                                imgProfile.setImageResource(R.drawable.man);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed to load profile data", Toast.LENGTH_SHORT).show();
                    }
                });

            requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomeFragment())
                        .commit();

                com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
                if (bottomNav != null) {
                    bottomNav.setSelectedItemId(R.id.bottom_nav_home);
                }
            }
        });

        ImageView btnEdit = view.findViewById(R.id.btnEdit);


        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new EditProfileFragment())
                        .addToBackStack(null)
                        .commit();

            }
        });

        android.widget.LinearLayout btnSignOut = view.findViewById(R.id.btnSignOut);
        if (btnSignOut != null) {
            btnSignOut.setOnClickListener(v -> {

                if (firebaseAuth != null) {
                    firebaseAuth.signOut();
                }

                com.google.android.material.bottomnavigation.BottomNavigationView bottomNavView = requireActivity().findViewById(R.id.bottomNavView);
                if (bottomNavView != null) {
                    bottomNavView.setSelectedItemId(R.id.bottom_nav_home);
                }
            });
        }


    }


@Override
public void onResume() {
    super.onResume();

    if (getActivity() instanceof androidx.appcompat.app.AppCompatActivity) {
        androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getActivity();
        if (activity.getSupportActionBar() != null) activity.getSupportActionBar().hide();
        View toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar != null) toolbar.setVisibility(View.GONE);
    }

    com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
    if (bottomNav != null) bottomNav.getMenu().findItem(R.id.bottom_profile).setEnabled(false);
}

    @Override
    public void onPause() {
        super.onPause();

        if (getActivity() instanceof androidx.appcompat.app.AppCompatActivity) {
            androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) activity.getSupportActionBar().show();
            View toolbar = activity.findViewById(R.id.toolbar);
            if (toolbar != null) toolbar.setVisibility(View.VISIBLE);
        }

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottomNavView);
        if (bottomNav != null) bottomNav.getMenu().findItem(R.id.bottom_profile).setEnabled(true);
    }
}