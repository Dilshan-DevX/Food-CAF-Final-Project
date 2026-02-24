package com.codex.foodcaf.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawertLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//      ImageView imageView = findViewById(R.id.profileImage);
//
//
//
//        Glide.with(this)
//                .asBitmap()
//                .load(R.drawable.profile)
//                .override(80)
//                .into(imageView);

        ImageView imageView = findViewById(R.id.nav_Logo);
        ImageView menuicon = findViewById(R.id.menu_Logo);



        Glide.with(this)
                .asBitmap()
                .load(R.drawable.applogo)
                .override(100)
                .into(imageView);

        Glide.with(this)
                .asBitmap()
                .load(R.drawable.more_vert_24px)
                .override(70)
                .into(menuicon);

    }

}