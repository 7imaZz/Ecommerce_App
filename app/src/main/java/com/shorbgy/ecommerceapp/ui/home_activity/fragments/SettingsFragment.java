package com.shorbgy.ecommerceapp.ui.home_activity.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.FragmentSettingsBinding;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.squareup.picasso.Picasso;
import java.util.Objects;


public class SettingsFragment extends Fragment {


    private FragmentSettingsBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);

        retrieveUserInfo();

        binding.closeTv.setOnClickListener(v ->
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack());

        binding.updateTv.setOnClickListener(v -> {
            updateUserInfo();
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
        });

        binding.infContainerCard.setOnClickListener(v -> ImagePicker.Companion.with(requireActivity())
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start());

        return binding.getRoot();
    }

    private void updateUserInfo(){

        viewModel.updateUserInfo(
                Objects.requireNonNull(binding.usernameEt.getText()).toString(),
                Objects.requireNonNull(binding.phoneNumberEt.getText()).toString(),
                Objects.requireNonNull(binding.addressEt.getText()).toString());
    }

    private void retrieveUserInfo(){

        viewModel.userMutableLiveData.observe(getViewLifecycleOwner(), user -> {
            binding.usernameEt.setText(user.getName());
            binding.phoneNumberEt.setText(user.getPhone_number());
            binding.addressEt.setText(user.getAddress());
            Picasso.get()
                    .load(user.getImage_url())
                    .placeholder(R.drawable.profile)
                    .into(binding.proProfileImg);
        });
    }
}