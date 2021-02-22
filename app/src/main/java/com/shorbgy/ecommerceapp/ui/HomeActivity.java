package com.shorbgy.ecommerceapp.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.pojo.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth auth;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView usernameTextView;
    private CircleImageView profileImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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

    private void retrieveUserInfo(){
        // Get a reference to our user
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        // Attach a listener to read the data at our user reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                usernameTextView.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.nav_cart){
            Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.nav_categories){
            Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.nav_orders){
            Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.nav_settings){
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.nav_logout){
            auth.signOut();
            startActivity(new Intent(HomeActivity.this, WelcomeActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}