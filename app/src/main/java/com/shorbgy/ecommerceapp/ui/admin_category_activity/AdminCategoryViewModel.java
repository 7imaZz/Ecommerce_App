package com.shorbgy.ecommerceapp.ui.admin_category_activity;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shorbgy.ecommerceapp.pojo.Product;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class AdminCategoryViewModel extends ViewModel{

    private static final String TAG = "AdminCategoryViewModel";

    private final StorageReference storageReference =
            FirebaseStorage.getInstance().getReference().child("Product Images");
    private final DatabaseReference productsReference =
            FirebaseDatabase.getInstance().getReference("Products");

    public MutableLiveData<Task<Void>> uploadProductTaskMutableLiveData = new MutableLiveData<>();


    public void storeProductInformation(Uri fileUri, Product product){

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());
        product.setDate(saveCurrentDate);

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());
        product.setTime(saveCurrentTime);

        String productRandomKey = String.valueOf(System.currentTimeMillis());
        product.setPid(productRandomKey);

        StorageReference filePath = storageReference.child(saveCurrentDate+saveCurrentTime+".jpg");

        final UploadTask uploadTask = filePath.putFile(fileUri);

        uploadTask.addOnFailureListener(e ->
                Log.d(TAG, "storeProductInformation: "+e.getMessage())).addOnSuccessListener(taskSnapshot -> {
                    Log.d(TAG, "storeProductInformation: Product Uploaded Successfully");

                    uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()){
                            throw Objects.requireNonNull(task.getException());
                        }
                        return filePath.getDownloadUrl();
                    }).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String imageUrl = Objects.requireNonNull(task.getResult()).toString();
                            product.setImage_url(imageUrl);
                            saveInfoToDatabase(product);
                        }
                    });
                });
    }

    public void saveInfoToDatabase(Product product){

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("pid", product.getPid());
        productMap.put("product_name", product.getProduct_name());
        productMap.put("price", product.getPrice());
        productMap.put("description", product.getDescription());
        productMap.put("pieces", product.getPieces());
        productMap.put("date", product.getDate());
        productMap.put("time", product.getTime());
        productMap.put("image_url", product.getImage_url());
        productMap.put("category", product.getCategory());

        productsReference.child(product.getPid()).updateChildren(productMap)
                .addOnCompleteListener(task ->
                        uploadProductTaskMutableLiveData.postValue(task));
    }
}
