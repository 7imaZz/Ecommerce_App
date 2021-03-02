package com.shorbgy.ecommerceapp.ui.home_activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.ui.welcome_activity.WelcomeActivity;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView usernameTextView;
    private CircleImageView profileImageView;
    public HomeViewModel viewModel;
    private Uri fileUri;

    public static NavigationView navigationView;

    public static boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        isAdmin = getIntent().getBooleanExtra(Constants.IS_ADMIN, false);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isAdmin) {

            drawer = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.getHeaderView(0);
            usernameTextView = headerView.findViewById(R.id.username_tv);
            profileImageView = headerView.findViewById(R.id.profile_image);

            mDrawerToggle = new ActionBarDrawerToggle(this,
                    drawer, toolbar,
                    R.string.nav_header_desc,
                    R.string.navigation_drawer_close);

            retrieveUserInfo();
        }


    }

    private void retrieveUserInfo(){

        viewModel.userMutableLiveData.observe(this, user -> {
            usernameTextView.setText(user.getName());
            Picasso.get()
                    .load(user.getImage_url())
                    .into(profileImageView);
        });
    }

    private void uploadImage(){
        viewModel.uploadImage(fileUri);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!isAdmin) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_cart){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.cartFragment);
        }else if (item.getItemId() == R.id.nav_home){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.nav_home);
        }else if (item.getItemId() == R.id.nav_categories){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.categoriesFragment);
        }else if (item.getItemId() == R.id.nav_orders){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.customerOrdersFragment);
        }else if (item.getItemId() == R.id.nav_settings){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.settingsFragment);
        }else if (item.getItemId() == R.id.nav_logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(HomeActivity.this, WelcomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            assert data != null;
            fileUri = data.getData();
            uploadImage();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}