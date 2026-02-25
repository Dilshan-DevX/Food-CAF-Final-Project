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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.codex.foodcaf.R;
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
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

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
            // Login කෝඩ් එක මෙතනට දාන්න
        } else if (itemId == R.id.side_nav_logout) {
            // Logout කෝඩ් එක මෙතනට දාන්න
        }

        // Side menu එක open වෙලා තියෙනවා නම් ඒක close කරන්න
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