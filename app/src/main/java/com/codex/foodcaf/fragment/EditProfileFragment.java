//package com.codex.foodcaf.fragment;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.provider.Settings;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.codex.foodcaf.R;
//import com.codex.foodcaf.model.User;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.Priority;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.material.imageview.ShapeableImageView;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Locale;
//
//public class EditProfileFragment extends Fragment implements OnMapReadyCallback {
//
//    private FirebaseAuth firebaseAuth;
//    private FirebaseFirestore db;
//
//    private GoogleMap mMap;
//    private static final int LOCATION_REQ_CODE = 100;
//    private FusedLocationProviderClient fusedLocationClient;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//
//        com.google.android.gms.maps.SupportMapFragment mapFragment =
//                (com.google.android.gms.maps.SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//
//        if (mapFragment != null) {
//            mapFragment.getMapAsync(this);
//        }
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//
//        ShapeableImageView imgProfile = view.findViewById(R.id.imgProfile);
//        TextInputEditText etName = view.findViewById(R.id.etName);
//        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
//        TextInputEditText etPhone = view.findViewById(R.id.etPhone);
//        android.widget.EditText etLocation = view.findViewById(R.id.LocationDetail);
//        ImageView setLocationAddress = view.findViewById(R.id.setLocationAddress);
//
//        if (etName != null) etName.setEnabled(false);
//        if (etEmail != null) etEmail.setEnabled(false);
//
//        if (currentUser != null) {
//            db.collection("users").document(currentUser.getUid()).get()
//                    .addOnSuccessListener(documentSnapshot -> {
//                        if (isAdded() && documentSnapshot.exists()) {
//                            User user = documentSnapshot.toObject(User.class);
//                            if (user != null) {
//                                if (etName != null && user.getName() != null) etName.setText(user.getName());
//                                if (etEmail != null && user.getEmail() != null) etEmail.setText(user.getEmail());
//                                if (etPhone != null && user.getMobileNum() != null) etPhone.setText(user.getMobileNum());
//                                if (etLocation != null && user.getAddress() != null) etLocation.setText(user.getAddress());
//
//                                String profilePicUrl = user.getProfilePicUrl();
//                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
//                                    Glide.with(requireContext())
//                                            .load(profilePicUrl)
//                                            .centerCrop()
//                                            .placeholder(R.drawable.man)
//                                            .error(R.drawable.man)
//                                            .into(imgProfile);
//                                } else {
//                                    if (imgProfile != null) imgProfile.setImageResource(R.drawable.man);
//                                }
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        if (isAdded()) Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
//                    });
//        }
//
//        if (setLocationAddress != null) {
//            setLocationAddress.setOnClickListener(v -> {
//                fetchAndSetCurrentLocation(etLocation);
//            });
//        }
//
//        ImageView btnBack = view.findViewById(R.id.btnBack);
//        if (btnBack != null) {
//            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
//        }
//
//        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
//            @Override
//            public void handleOnBackPressed() {
//                requireActivity().getSupportFragmentManager().popBackStack();
//            }
//        });
//
//        com.google.android.material.button.MaterialButton btnSave = view.findViewById(R.id.btnSave);
//        if (btnSave != null) {
//            btnSave.setOnClickListener(v -> {
//                if (currentUser != null) {
//
//                    // User enter maadida hosa details tagolli
//                    String updatedPhone = "";
//                    if (etPhone != null && etPhone.getText() != null) {
//                        updatedPhone = etPhone.getText().toString().trim();
//                    }
//
//                    String updatedLocation = "";
//                    if (etLocation != null && etLocation.getText() != null) {
//                        updatedLocation = etLocation.getText().toString().trim();
//                    }
//
//                    // Empty check maadi
//                    if (updatedPhone.isEmpty() || updatedLocation.isEmpty()) {
//                        Toast.makeText(requireContext(), "Please enter phone number and location", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//                    Toast.makeText(requireContext(), "Updating profile...", Toast.LENGTH_SHORT).show();
//
//                    // Firestore nalli data update maadi
//                    db.collection("users").document(currentUser.getUid())
//                            .update(
//                                    "mobileNum", updatedPhone,
//                                    "address", updatedLocation
//                            )
//                            .addOnSuccessListener(aVoid -> {
//                                if (isAdded()) {
//                                    Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
//                                    // Update aadmele hinde (Profile page ge) hogi
//                                    requireActivity().getSupportFragmentManager().popBackStack();
//                                }
//                            })
//                            .addOnFailureListener(e -> {
//                                if (isAdded()) {
//                                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                }
//            });
//        }
//    }
//
//    private void fetchAndSetCurrentLocation(android.widget.EditText etLocation) {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
//            return;
//        }
//
//        // GPS ON aagi irukka nu check pandrathu
//        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(requireContext(), "Please turn on your Location/GPS", Toast.LENGTH_LONG).show();
//            // GPS on panna settings page-ku redirect pandrathu
//            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//            return;
//        }
//
//        Toast.makeText(requireContext(), "Fetching location...", Toast.LENGTH_SHORT).show();
//
//        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                .addOnSuccessListener(location -> {
//                    if (location != null) {
//                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//                        if (mMap != null) {
//                            mMap.clear();
//                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("My Location"));
//                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
//                        }
//
//                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
//                        try {
//                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                            if (addresses != null && !addresses.isEmpty()) {
//                                Address address = addresses.get(0);
//                                String fullAddress = address.getAddressLine(0);
//                                if (etLocation != null) {
//                                    etLocation.setText(fullAddress);
//                                }
//                            } else {
//                                Toast.makeText(requireContext(), "Location details not found", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Toast.makeText(requireContext(), "Network error, couldn't fetch address", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(requireContext(), "Please try again later", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    Toast.makeText(requireContext(), "Failed to get location", Toast.LENGTH_SHORT).show();
//                });
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == LOCATION_REQ_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                android.widget.EditText etLocation = getView() != null ? getView().findViewById(R.id.LocationDetail) : null;
//                if (etLocation != null) {
//                    fetchAndSetCurrentLocation(etLocation);
//                }
//            } else {
//                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (getActivity() instanceof androidx.appcompat.app.AppCompatActivity) {
//            androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getActivity();
//            if (activity.getSupportActionBar() != null) activity.getSupportActionBar().hide();
//            View toolbar = activity.findViewById(R.id.toolbar);
//            if (toolbar != null) toolbar.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        if (getActivity() instanceof androidx.appcompat.app.AppCompatActivity) {
//            androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getActivity();
//            if (activity.getSupportActionBar() != null) activity.getSupportActionBar().show();
//            View toolbar = activity.findViewById(R.id.toolbar);
//            if (toolbar != null) toolbar.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public void onMapReady(@NonNull GoogleMap googleMap) {
//        mMap = googleMap;
//        mMap.getUiSettings().setMapToolbarEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//    }
//}
//


package com.codex.foodcaf.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfileFragment extends Fragment implements OnMapReadyCallback {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private GoogleMap mMap;
    private static final int LOCATION_REQ_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;

    //SharedPreferences
    private ActivityResultLauncher<Intent> galleryLauncher;
    private SharedPreferences sharedPreferences;
    private ShapeableImageView imgProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {

                            Glide.with(requireContext()).load(selectedImageUri).into(imgProfile);

                            sharedPreferences.edit().putString("temp_profile_image", selectedImageUri.toString()).apply();
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        com.google.android.gms.maps.SupportMapFragment mapFragment =
                (com.google.android.gms.maps.SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        imgProfile = view.findViewById(R.id.imgProfile);

        ImageView profileImageUpdate = view.findViewById(R.id.profileImageUpdate);
        TextInputEditText etName = view.findViewById(R.id.etName);
        TextInputEditText etEmail = view.findViewById(R.id.etEmail);
        TextInputEditText etPhone = view.findViewById(R.id.etPhone);
        android.widget.EditText etLocation = view.findViewById(R.id.LocationDetail);
        ImageView setLocationAddress = view.findViewById(R.id.setLocationAddress);

        if (etName != null) etName.setEnabled(false);
        if (etEmail != null) etEmail.setEnabled(false);


        sharedPreferences.edit().remove("temp_profile_image").apply();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (isAdded() && documentSnapshot.exists()) {
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null) {
                                if (etName != null && user.getName() != null) etName.setText(user.getName());
                                if (etEmail != null && user.getEmail() != null) etEmail.setText(user.getEmail());
                                if (etPhone != null && user.getMobileNum() != null) etPhone.setText(user.getMobileNum());
                                if (etLocation != null && user.getAddress() != null) etLocation.setText(user.getAddress());

                                String profilePicUrl = user.getProfilePicUrl();
                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                    Glide.with(requireContext())
                                            .load(profilePicUrl)
                                            .centerCrop()
                                            .placeholder(R.drawable.man)
                                            .error(R.drawable.man)
                                            .into(imgProfile);
                                } else {
                                    if (imgProfile != null) imgProfile.setImageResource(R.drawable.man);
                                }
                            }
                        }
                    });
        }


        if (profileImageUpdate != null) {
            profileImageUpdate.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intent);
            });
        }

        if (setLocationAddress != null) {
            setLocationAddress.setOnClickListener(v -> {
                fetchAndSetCurrentLocation(etLocation);
            });
        }

        ImageView btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        }

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        com.google.android.material.button.MaterialButton btnSave = view.findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                if (currentUser == null) return;

                String updatedPhone = etPhone != null && etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
                String updatedLocation = etLocation != null && etLocation.getText() != null ? etLocation.getText().toString().trim() : "";

                if (updatedPhone.isEmpty() || updatedLocation.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter phone number and location", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(requireContext(), "Updating profile...", Toast.LENGTH_SHORT).show();

                String savedImageUri = sharedPreferences.getString("temp_profile_image", null);

                if (savedImageUri != null) {

                    StorageReference fileRef = storage.getReference().child("profile_images/" + currentUser.getUid() + ".jpg");

                    fileRef.putFile(Uri.parse(savedImageUri))
                            .addOnSuccessListener(taskSnapshot -> {

                                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    String downloadUrl = uri.toString();
                                    updateFirestoreData(currentUser.getUid(), updatedPhone, updatedLocation, downloadUrl);
                                });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    updateFirestoreData(currentUser.getUid(), updatedPhone, updatedLocation, null);
                }
            });
        }
    }


    private void updateFirestoreData(String uid, String phone, String address, String profilePicUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("mobileNum", phone);
        updates.put("address", address);

        if (profilePicUrl != null) {
            updates.put("profilePicUrl", profilePicUrl);
        }

        db.collection("users").document(uid).update(updates)
                .addOnSuccessListener(aVoid -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();

                        sharedPreferences.edit().remove("temp_profile_image").apply();

                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchAndSetCurrentLocation(android.widget.EditText etLocation) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
            return;
        }

        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(requireContext(), "Please turn on your Location/GPS", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return;
        }

        Toast.makeText(requireContext(), "Fetching location...", Toast.LENGTH_SHORT).show();

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                        if (mMap != null) {
                            mMap.clear();
                            mMap.addMarker(new MarkerOptions().position(currentLatLng).title("My Location"));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                        }

                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String fullAddress = address.getAddressLine(0);
                                if (etLocation != null) etLocation.setText(fullAddress);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQ_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            android.widget.EditText etLocation = getView() != null ? getView().findViewById(R.id.LocationDetail) : null;
            if (etLocation != null) fetchAndSetCurrentLocation(etLocation);
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
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
    }
}