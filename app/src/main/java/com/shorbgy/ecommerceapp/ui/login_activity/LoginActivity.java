package com.shorbgy.ecommerceapp.ui.login_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.ActivityLoginBinding;
import com.shorbgy.ecommerceapp.ui.admin_activity.AdminActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;

import java.util.Objects;

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

        binding.forgotTv.setOnClickListener(v -> forgotPassword());
    }

    private void loginAsAdmin() {
        if(Objects.requireNonNull(binding.emailLoginEt.getText()).toString().equals("admin")
                && Objects.requireNonNull(binding.passwordLoginEt.getText()).toString().equals("admin")){
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
                    Objects.requireNonNull(binding.emailLoginEt.getText()).toString(),
                    Objects.requireNonNull(binding.passwordLoginEt.getText()).toString())
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                        if (task.isSuccessful()){
                            Intent intent = new Intent(this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(this, "Invalid Email Or Password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void forgotPassword(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Forgot Password?");

        EditText editText = new EditText(this);
        editText.setHint("Email Address");

        builder.setView(editText);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            if (editText.getText().toString().isEmpty()){
                Toast.makeText(LoginActivity.this,
                        "Please Enter Your Email Address", Toast.LENGTH_LONG).show();
            }else{

                ProgressDialog progressDialog
                        = new ProgressDialog(LoginActivity.this);
                progressDialog.setTitle("Please Wait...");
                progressDialog.show();

                auth.sendPasswordResetEmail(editText.getText().toString()).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this,
                                "Please Check Your Email Address", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(LoginActivity.this,
                                "Failed", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
}