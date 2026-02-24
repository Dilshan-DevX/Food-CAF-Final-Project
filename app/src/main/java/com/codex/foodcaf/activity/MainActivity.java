package com.codex.foodcaf.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

     private DrawerLayout     drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;

    private ImageView imageView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        ImageView menuicon = findViewById(R.id.menu_Logo);

        drawerLayout = findViewById(R.id.drawertLayout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.sideNavView);
        bottomNavigationView = findViewById(R.id.bottomNavView);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);

        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();




        ImageView imageView = findViewById(R.id.nav_Logo);

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

        menuicon.setOnClickListener(view -> {
            drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
        });


        getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                Toast.makeText(MainActivity.this, "Back pressed", Toast.LENGTH_SHORT).show();
                if(drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)){
                    drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
                }else{
                    finish();
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(this);
        bottomNavigationView.setOnItemSelectedListener(this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.side_nav_home | R.id.bottom_nav_home:
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.side_nav_profile | R.id.bottom_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;
                case R.id.side_nav_favorite:
                Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT).show();
                break;


        }

        return false;
    }
}