package com.shorbgy.ecommerceapp.ui;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.pojo.User;
import com.shorbgy.ecommerceapp.utils.Constants;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.navigation.Navigation;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private TextView usernameTextView;
    private CircleImageView profileImageView;

    public static NavigationView navigationView;

    private Uri fileUri;
    private String imageUrl;


    public static boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        isAdmin = getIntent().getBooleanExtra(Constants.IS_ADMIN, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!isAdmin) {
            storageReference = FirebaseStorage.getInstance().getReference().child("Profile Images");
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");

            auth = FirebaseAuth.getInstance();

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
        DatabaseReference ref = databaseReference
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        // Attach a listener to read the data at our user reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                usernameTextView.setText(user.getName());
                Picasso.get()
                        .load(user.getImage_url())
                        .into(profileImageView);
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
            Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId() == R.id.nav_orders){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.customerOrdersFragment);
        }else if (item.getItemId() == R.id.nav_settings){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.settingsFragment);
        }else if (item.getItemId() == R.id.nav_logout){
            auth.signOut();
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

    private void uploadImage(){

        StorageReference filePath = storageReference
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+".jpg");

        final UploadTask uploadTask = filePath.putFile(fileUri);

        uploadTask.addOnFailureListener(e ->
                Toast.makeText(HomeActivity.this,
                        e.getMessage(), Toast.LENGTH_SHORT).show())
                .addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {

            if (!task.isSuccessful()){
                throw Objects.requireNonNull(task.getException());
            }

            return filePath.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                imageUrl = Objects.requireNonNull(task.getResult()).toString();
                HashMap<String, Object> imageMap = new HashMap<>();
                imageMap.put("image_url", imageUrl);
                databaseReference.child(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                        .updateChildren(imageMap).addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()){
                                Toast.makeText(HomeActivity.this,
                                        Objects.requireNonNull(task1.getException()).getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                });
            }
        }));
    }
}