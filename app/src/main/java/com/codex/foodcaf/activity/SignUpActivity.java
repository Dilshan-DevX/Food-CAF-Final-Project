package com.codex.foodcaf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.ActivitySignUpBinding;
import com.codex.foodcaf.databinding.ActivitySigninBinding;
import com.codex.foodcaf.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    private FirebaseAuth firebaseAuth;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =  ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        binding.signupTxtSignin.setOnClickListener(view -> {
           Intent intent = new Intent(SignUpActivity.this,SigninActivity.class);
           startActivity(intent);
           finish();
        });

        binding.signupBtnSignup.setOnClickListener(view -> {

            String name = binding.signupInputName.getText().toString().trim();
            String email = binding.signupInputEmail.getText().toString().trim();
            String password = binding.signupInputPassword.getText().toString().trim();
            String confirmPassword = binding.signupInputConfirmPassword.getText().toString().trim();

            String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
            String usernamePattern = "^[a-zA-Z0-9_]{3,15}$";



            if (name.isEmpty()) {
                binding.signupInputName.setError("Name cannot be empty!");
                binding.signupInputName.requestFocus();
                return;

            } else if (name.length() < 3) {
                binding.signupInputName.setError("Name must be at least 3 characters long!");
                binding.signupInputName.requestFocus();
                return;
            } else if (!name.matches(usernamePattern)) {
                binding.signupInputName.setError("Name can only contain letters, numbers, and underscores (no spaces)!");
                binding.signupInputName.requestFocus();
                return;
            }

            if (email.isEmpty()){
                binding.signupInputEmail.setError("Email is required");
                binding.signupInputEmail.requestFocus();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.signupInputEmail.setError("Enter Valid Email");
                binding.signupInputEmail.requestFocus();
                return;
            }


            if (password.isEmpty()) {
                binding.signupInputPassword.setError("Password is required");
                binding.signupInputPassword.requestFocus();
                return;

            } else if (!password.matches(passwordPattern)) {
                binding.signupInputPassword.setError("Password is too weak! It must contain at least 6 characters, one uppercase, one lowercase, one number and one special character.");
                binding.signupInputPassword.requestFocus();
                return;
            }

            if (confirmPassword.isEmpty()) {
                binding.signupInputConfirmPassword.setError("Password is required");
                binding.signupInputConfirmPassword.requestFocus();
                return;

            }  else if (!password.equals(confirmPassword)) {
                binding.signupInputConfirmPassword.setError("Passwords do not match!");
                binding.signupInputConfirmPassword.requestFocus();
                return;
            }
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        String uid = task.getResult().getUser().getUid();

                        User user = User.builder()
                                .uId(uid)
                                .name(name)
                                .email(email).build();

                        firebaseFirestore.collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SignUpActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this,OpeningScreen1.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "User Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        });

    }
}