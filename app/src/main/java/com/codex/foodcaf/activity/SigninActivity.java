package com.codex.foodcaf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    private ActivitySigninBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySigninBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        binding.signinTxtSignup.setOnClickListener(view -> {
            Intent intent = new Intent(SigninActivity.this,SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        binding.signinBtnSignin.setOnClickListener(view -> {

            String email = binding.signinInputEmail.getText().toString();
            String password = binding.signinInputPassword.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                         updateUI(firebaseAuth.getCurrentUser());
//                        Intent intent = new Intent(SigninActivity.this,MainActivity.class);
//                        startActivity(intent);
//                        finish();
                    }else {
                        Toast.makeText(SigninActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        });

//        findViewById(R.id.signin_txt_signup).setOnClickListener(view -> {
//
//        });

    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            Intent intent = new Intent(SigninActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
        }
    }
}