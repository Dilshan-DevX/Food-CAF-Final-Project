package com.codex.foodcaf.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.codex.foodcaf.R;

public class OpeningScreen1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen1);

        findViewById(R.id.intro_btn_next).setOnClickListener(view -> {
            Intent intent = new Intent(OpeningScreen1.this,OpeningScreen2.class);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.intro_txt_skip).setOnClickListener(view -> {
            Intent intent = new Intent(OpeningScreen1.this,SigninActivity.class);
            startActivity(intent);
            finish();
        });

    }
}