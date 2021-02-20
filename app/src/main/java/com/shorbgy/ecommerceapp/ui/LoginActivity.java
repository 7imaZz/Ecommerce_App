package com.shorbgy.ecommerceapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;

    private boolean isAdmin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        binding.adminTv.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            binding.customerTv.setVisibility(View.VISIBLE);
            binding.forgotTv.setVisibility(View.GONE);
            binding.labelTv.setText(getString(R.string.login_to_e_commerce_app_as_admin));
            isAdmin = true;
        });

        binding.customerTv.setOnClickListener(v -> {
            v.setVisibility(View.GONE);
            binding.adminTv.setVisibility(View.VISIBLE);
            binding.forgotTv.setVisibility(View.VISIBLE);
            binding.labelTv.setText(getString(R.string.login_to_e_commerce_app));
            isAdmin = false;
        });

        binding.loginBtn.setOnClickListener(v -> {
            if (!isAdmin) {
                loginAsCustomer();
            }else{
                loginAsAdmin();
            }
        });
    }

    private void loginAsAdmin() {
        if(binding.emailLoginEt.getText().toString().equals("admin")
                && binding.passwordLoginEt.getText().toString().equals("admin")){
            Intent intent = new Intent(this, AdminActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginAsCustomer() {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Logging In");
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        if (TextUtils.isEmpty(binding.emailLoginEt.getText())){
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(binding.passwordLoginEt.getText())){
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }else {
            auth.signInWithEmailAndPassword(
                    binding.emailLoginEt.getText().toString(),
                    binding.passwordLoginEt.getText().toString()
            ).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}