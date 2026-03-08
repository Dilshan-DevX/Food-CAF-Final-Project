package com.codex.foodcaf.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.activity.MainActivity;
import com.codex.foodcaf.databinding.FragmentCheckOutBinding;

public class CheckOutFragment extends Fragment {

    private FragmentCheckOutBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 🔴 මෙතන UI / Binding කෝඩ් මුකුත් ලියන්නේ නැහැ 🔴
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // මෙතනදී Layout එක Load කරනවා
        binding = FragmentCheckOutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // 🟢 සියලුම Button Clicks ලියන්නේ මේ onViewCreated එක ඇතුළෙයි 🟢
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(requireContext())
                .load(R.drawable.gmap)
                .into(binding.imgMap);

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        /// ///////////////////////////////Address enable //////////////////////////////////////////////////

        binding.btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // 2. Edit Address බොත්තම එබුවම වෙන දේ
        binding.btnEditAddress.setOnClickListener(v -> {
            boolean isEnabled = binding.txtAddressDetail.isEnabled();

            if (!isEnabled) {
                // Edit කරන්න දෙනවා (Enable කරනවා)
                binding.txtAddressDetail.setEnabled(true);
                binding.txtAddressDetail.requestFocus();

                // Icon එක 'Save' එකකට මාරු කරනවා
                binding.btnEditAddress.setImageResource(android.R.drawable.ic_menu_save);

                // Keyboard එක ඔටෝ උඩට ගන්නවා
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.txtAddressDetail, InputMethodManager.SHOW_IMPLICIT);

            } else {
                // ආයෙත් Disable කරනවා (Save කළාම)
                binding.txtAddressDetail.setEnabled(false);

                // Icon එක ආයෙත් පරණ 'Edit' එකටම මාරු කරනවා
                binding.btnEditAddress.setImageResource(android.R.drawable.ic_menu_edit);

                // Keyboard එක පල්ලෙහාට දානවා
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.txtAddressDetail.getWindowToken(), 0);

                // 🔴 අලුත් Address එක ගන්න ඕනේ නම් මේක පාවිච්චි කරන්න:
                // String newAddress = binding.txtAddressDetail.getText().toString();
            }
        });

        /// ///////////////////////////////Details enable //////////////////////////////////////////////////

        binding.btnEditDetails.setOnClickListener(v -> {
            boolean isEnabled = binding.txtName.isEnabled();

            if (!isEnabled) {
                // 1. Text boxes තුනම Type කරන්න පුළුවන් විදියට Enable කරනවා
                binding.txtName.setEnabled(true);
                binding.txtEmail.setEnabled(true);
                binding.txtConNum.setEnabled(true);

                // 2. පළවෙනි Text box එකට (නම) cursor එක ගේනවා
                binding.txtName.requestFocus();

                // 3. Icon එක 'Save' (හරි ලකුණක් වගේ) එකකට මාරු කරනවා
                binding.btnEditDetails.setImageResource(android.R.drawable.ic_menu_save);

                // 4. Keyboard එක ඔටෝ උඩට ගන්නවා
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(binding.txtName, InputMethodManager.SHOW_IMPLICIT);

            } else {
                // 1. Save කළාට පස්සේ ආයෙත් තුනම Disable කරනවා
                binding.txtName.setEnabled(false);
                binding.txtEmail.setEnabled(false);
                binding.txtConNum.setEnabled(false);

                // 2. Icon එක ආයෙත් පරණ 'Edit' එකටම මාරු කරනවා
                // (ඔයාගේ XML එකේ දීලා තියෙන්නේ edit_24px නිසා ඒකම දුන්නා)
                binding.btnEditDetails.setImageResource(R.drawable.edit_24px);

                // 3. Keyboard එක පල්ලෙහාට දානවා
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.txtName.getWindowToken(), 0);

                // 🔴 Save කරපු අලුත් විස්තර ටික ගන්න ඕනේ නම්:
                // String newName = binding.txtName.getText().toString();
                // String newEmail = binding.txtEmail.getText().toString();
                // String newNumber = binding.txtConNum.getText().toString();
            }
        });

        // 3. Confirm Order බොත්තම
        binding.btnConfirmOrder.setOnClickListener(v -> {
            // ඔයාගේ Order එක Database එකට Save කරන කෝඩ් එක මෙතන ලියන්න
        });
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