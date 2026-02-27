package com.codex.foodcaf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
import com.codex.foodcaf.databinding.ActivityMainBinding;
import com.codex.foodcaf.databinding.SideNavHeaderBinding;
import com.codex.foodcaf.fragment.CartFragment;
import com.codex.foodcaf.fragment.CategoryFragment;
import com.codex.foodcaf.fragment.FavFragment;
import com.codex.foodcaf.fragment.HomeFragment;
import com.codex.foodcaf.fragment.MessageFragment;
import com.codex.foodcaf.fragment.OrderFragment;
import com.codex.foodcaf.fragment.ProfileFragment;
import com.codex.foodcaf.fragment.SettingsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NavigationBarView.OnItemSelectedListener {

    private ActivityMainBinding binding;
    private SideNavHeaderBinding sideNavHeaderBinding;
     private DrawerLayout     drawerLayout;
    private MaterialToolbar toolbar;
    private NavigationView navigationView;

    private ImageView imageView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View headerView = binding.sideNavView.getHeaderView(0);
        sideNavHeaderBinding = SideNavHeaderBinding.bind(headerView);

        imageView = findViewById(R.id.nav_Logo);

        ImageView menuicon = findViewById(R.id.menu_Logo);

        drawerLayout = binding.drawertLayout;
        toolbar = binding.toolbar;
        navigationView = binding.sideNavView;
        bottomNavigationView = binding.bottomNavView;


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
                .override(80)
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

        if (savedInstanceState == null){
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.side_nav_home);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        Menu navMenu = navigationView.getMenu();
        Menu bottommenu = bottomNavigationView.getMenu();

        for (int i = 0; i < navMenu.size(); i++){
            navMenu.getItem(i).setChecked(false);
        }


        for (int i = 0; i < bottommenu.size(); i++){
            bottommenu.getItem(i).setChecked(false);
        }

        if (itemId == R.id.side_nav_home || itemId == R.id.bottom_nav_home) {
            loadFragment(new HomeFragment());
            navigationView.setCheckedItem(R.id.side_nav_home);
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_home).setChecked(true);

        } else if (itemId == R.id.bottom_nav_search) {
            loadFragment(new CategoryFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_nav_search).setChecked(true);

        } else if (itemId == R.id.bottom_order) {
            loadFragment(new OrderFragment());
            bottomNavigationView.getMenu().findItem(R.id.bottom_order).setChecked(true);

        } else if (itemId == R.id.side_nav_profile || itemId == R.id.bottom_profile) {
            loadFragment(new ProfileFragment());
            navigationView.setCheckedItem(R.id.side_nav_profile);
            bottomNavigationView.getMenu().findItem(R.id.bottom_profile).setChecked(true);

        } else if (itemId == R.id.side_nav_favorite) {
            loadFragment(new FavFragment());
            navigationView.setCheckedItem(R.id.side_nav_favorite);

        } else if (itemId == R.id.side_nav_cart) {
            loadFragment(new CartFragment());
            navigationView.setCheckedItem(R.id.side_nav_cart);

        } else if (itemId == R.id.side_nav_message) {
            loadFragment(new MessageFragment());
            navigationView.setCheckedItem(R.id.side_nav_message);

        } else if (itemId == R.id.side_nav_settings) {
            loadFragment(new SettingsFragment());
            navigationView.setCheckedItem(R.id.side_nav_settings);

        } else if (itemId == R.id.side_nav_login) {

            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);

        } else if (itemId == R.id.side_nav_logout) {

            Intent intent = new Intent(MainActivity.this, SigninActivity.class);
            startActivity(intent);

        }


        if(drawerLayout.isDrawerOpen(androidx.core.view.GravityCompat.START)){
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
        }

        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer,fragment);
        transaction.commit();

        /// single line execution
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
    }
}