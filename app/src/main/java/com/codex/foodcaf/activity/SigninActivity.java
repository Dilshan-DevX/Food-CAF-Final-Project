package com.codex.foodcaf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore; // 🔴 අලුතෙන් එකතු කළා

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db; // 🔴 අලුතෙන් එකතු කළා

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // 🔴 Initialize කළා

        binding.signinTxtSignup.setOnClickListener(view -> {
            Intent intent = new Intent(SigninActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        binding.signinBtnSignin.setOnClickListener(view -> {

            String email = binding.signinInputEmail.getText().toString().trim();
            String password = binding.signinInputPassword.getText().toString().trim();

            if (email.isEmpty()) {
                binding.signinInputEmail.setError("Email is required");
                binding.signinInputEmail.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                binding.signinInputPassword.setError("Password is required");
                binding.signinInputPassword.requestFocus();
                return;
            }

            // 1. Email සහ Password එකෙන් ලොග් වෙන්න බලනවා
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                        if (currentUser != null) {
                            String uid = currentUser.getUid();

                            // 2. Authentication හරි නම්, Firestore එකෙන් Status එක බලනවා
                            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    // Status එක ගන්නවා (boolean)
                                    Boolean status = documentSnapshot.getBoolean("status");

                                    // 3. Status එක true ද බලනවා
                                    if (status != null && status) {
                                        // Status true නම් විතරක් ඇතුළට යවනවා
                                        updateUI(currentUser);
                                    } else {
                                        // Status false නම් ආපහු Sign out කරලා මැසේජ් එකක් දෙනවා
                                        firebaseAuth.signOut();
                                        Toast.makeText(SigninActivity.this, "Your account is Suspended. Please contact admin.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    firebaseAuth.signOut();
                                    Toast.makeText(SigninActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(e -> {
                                firebaseAuth.signOut();
                                Toast.makeText(SigninActivity.this, "Error checking account status.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        Toast.makeText(SigninActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            Intent intent = new Intent(SigninActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    }
}