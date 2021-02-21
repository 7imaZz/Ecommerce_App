package com.shorbgy.ecommerceapp.ui;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.ActivityAdminCategoryBinding;
import com.shorbgy.ecommerceapp.utils.Constants;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AdminCategory extends AppCompatActivity {


    private ActivityAdminCategoryBinding binding;

    private StorageReference storageReference;
    private DatabaseReference root;

    private Uri fileUri;
    private String imageUrl;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_category);

        storageReference = FirebaseStorage.getInstance().getReference().child("Product Images");
        root = FirebaseDatabase.getInstance().getReference("Products");

        categoryName = getIntent().getStringExtra(Constants.CATEGORY_NAME);

        binding.uploadBtn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.productName.getText())){
                Toast.makeText(this, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.productDesc.getText())){
                Toast.makeText(this, "Please Enter Product Description", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.productPrice.getText())){
                Toast.makeText(this, "Please Enter Product Price", Toast.LENGTH_SHORT).show();
            }else if (fileUri == null){
                Toast.makeText(this, "Please Choose Pick Image For Your Product", Toast.LENGTH_SHORT).show();
            }else{
                storeProductInformation();
            }
        });

        binding.productImage.setOnClickListener(v -> ImagePicker.Companion.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start());
    }

    private void storeProductInformation(){

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading product");
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String productRandomKey = saveCurrentDate+saveCurrentTime;

        StorageReference filePath = storageReference.child(productRandomKey+".jpg");

        final UploadTask uploadTask = filePath.putFile(fileUri);

        uploadTask.addOnFailureListener(e -> {
            Toast.makeText(AdminCategory.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }).addOnSuccessListener(taskSnapshot -> {
            Toast.makeText(AdminCategory.this, "Product Uploaded Successfully", Toast.LENGTH_SHORT).show();

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()){
                    throw task.getException();
                }

                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    imageUrl = task.getResult().toString();
                    saveInfoToDatabase(productRandomKey,  saveCurrentDate, saveCurrentTime);
                }
            });

        });
    }

    private void saveInfoToDatabase(String productRandomKey, String currentDate, String currentTime){

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pid", productRandomKey);
        productMap.put("product_name", binding.productName.getText().toString());
        productMap.put("price", binding.productPrice.getText().toString()+"$");
        productMap.put("description", binding.productDesc.getText().toString());
        productMap.put("date", currentDate);
        productMap.put("time", currentTime);
        productMap.put("image_url", imageUrl);
        productMap.put("category", categoryName);

        root.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()){
                        Log.d("Upload", "onComplete: "+task.getException().getMessage());
                    }
                });

        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            fileUri = data.getData();
            binding.productImage.setImageURI(fileUri);

            //You can get File object from intent
            File file = ImagePicker.Companion.getFile(data);

            //You can also get File Path from intent
            String filePath = ImagePicker.Companion.getFilePath(data);


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}