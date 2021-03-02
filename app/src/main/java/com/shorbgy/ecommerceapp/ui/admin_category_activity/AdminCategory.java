package com.shorbgy.ecommerceapp.ui.admin_category_activity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.databinding.ActivityAdminCategoryBinding;
import com.shorbgy.ecommerceapp.pojo.Product;
import java.util.Objects;

public class AdminCategory extends AppCompatActivity {


    private ActivityAdminCategoryBinding binding;
    private AdminCategoryViewModel viewModel;

    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_admin_category);

        viewModel = new ViewModelProvider(this).get(AdminCategoryViewModel.class);


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
                Product product = new Product();
                product.setProduct_name(Objects.requireNonNull(binding.productName.getText()).toString());
                product.setPrice(Objects.requireNonNull(binding.productPrice.getText()).toString());
                product.setDescription(Objects.requireNonNull(binding.productDesc.getText()).toString());
                product.setPieces(Objects.requireNonNull(binding.pieces.getText()).toString());
                storeProductInformation(product);
            }
        });

        binding.productImage.setOnClickListener(v -> ImagePicker.Companion.with(this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start());
    }

    private void storeProductInformation(Product product){


        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Uploading product");
        dialog.setMessage("Please Wait...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        viewModel.storeProductInformation(fileUri, product);

        viewModel.uploadProductTaskMutableLiveData.observe(this, task -> {
            dialog.dismiss();
            if (task.isSuccessful()){
                Toast.makeText(AdminCategory.this, "Product Uploaded Successfully",
                        Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(AdminCategory.this, "Failed", Toast.LENGTH_SHORT).show();
            }

            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            assert data != null;
            fileUri = data.getData();
            binding.productImage.setImageURI(fileUri);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}