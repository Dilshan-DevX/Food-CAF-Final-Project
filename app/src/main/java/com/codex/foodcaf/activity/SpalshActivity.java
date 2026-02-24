package com.codex.foodcaf.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.squareup.picasso.Picasso;

public class SpalshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            getWindow().setDecorFitsSystemWindows(false);

            WindowInsetsController Controller = getWindow().getInsetsController();
            if (Controller != null) {
                Controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            }
        }else {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
        setContentView(R.layout.activity_spalsh);

        ImageView imageView = findViewById(R.id.spalshLogo);

//        Picasso.get()
//                .load(R.drawable.ic_launcher_foreground)
//                .resize(300,300)
//                .into(imageView);

        Glide.with(this)
                .asBitmap()
                .load(R.drawable.applogo)
                .override(250)
                .into(imageView);

        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.progress_circular).setVisibility(View.VISIBLE);

                    }
                },500);

        new Handler(Looper.getMainLooper())
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.progress_circular).setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(SpalshActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },3000);
    }

}