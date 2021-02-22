package com.shorbgy.ecommerceapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentSettingsBinding;
import com.shorbgy.ecommerceapp.pojo.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;


public class SettingsFragment extends Fragment {


    private FragmentSettingsBinding binding;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        retrieveUserInfo();

        binding.closeTv.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_home));

        binding.updateTv.setOnClickListener(v -> {
            updateUserInfo();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_home);
        });

        binding.infContainerCard.setOnClickListener(v -> ImagePicker.Companion.with(requireActivity())
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start());
    }

    private void updateUserInfo(){
        HashMap<String, Object> userInfo = new HashMap<>();

        userInfo.put("name", Objects.requireNonNull(binding.usernameEt.getText()).toString());
        userInfo.put("phone_number", Objects.requireNonNull(binding.phoneNumberEt.getText()).toString());
        userInfo.put("address", Objects.requireNonNull(binding.addressEt.getText()).toString());

        databaseReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .updateChildren(userInfo).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        Toast.makeText(requireContext(),
                                Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
        });
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
                binding.usernameEt.setText(user.getName());
                binding.phoneNumberEt.setText(user.getPhone_number());
                binding.addressEt.setText(user.getAddress());
                Picasso.get()
                        .load(user.getImage_url())
                        .into(binding.proProfileImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}