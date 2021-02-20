package com.shorbgy.ecommerceapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shorbgy.ecommerceapp.databinding.ActivityRegisterBinding;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        root = FirebaseDatabase.getInstance().getReference("Users");

        binding.createAccountBtn.setOnClickListener(v -> createAccount());
    }

    private void createAccount(){
        if (TextUtils.isEmpty(binding.usernameEt.getText())){
            Toast.makeText(this, "Please Enter Username", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(binding.emailEt.getText())){
            Toast.makeText(this, "Please Enter Your E-mail", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(binding.phoneNumberEt.getText())){
            Toast.makeText(this, "Please Enter Your Phone Number", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(binding.passwordEt.getText())){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
        }else{

            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Checking User Info");
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            auth.createUserWithEmailAndPassword(
                    binding.emailEt.getText().toString(),
                    binding.passwordEt.getText().toString()
            ).addOnCompleteListener(task -> {
                if (task.isSuccessful()){

                    HashMap<String, String> userInfo = new HashMap<>();
                    userInfo.put("name", binding.usernameEt.getText().toString());
                    userInfo.put("email", binding.emailEt.getText().toString());
                    userInfo.put("phone_number", binding.phoneNumberEt.getText().toString());
                    userInfo.put("password", binding.passwordEt.getText().toString());

                    root.child(auth.getCurrentUser().getUid()).setValue(userInfo).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            Toast.makeText(this,
                                    "Email Created Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }else{
                            Toast.makeText(this, "Error has been occurred", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    });
                }else{
                    Toast.makeText(this, "Invalid User Info", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
        }
    }
}