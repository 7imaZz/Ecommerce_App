package com.shorbgy.ecommerceapp.ui.home_activity;
import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
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
import com.shorbgy.ecommerceapp.pojo.Cart;
import com.shorbgy.ecommerceapp.pojo.Category;
import com.shorbgy.ecommerceapp.pojo.Order;
import com.shorbgy.ecommerceapp.pojo.Product;
import com.shorbgy.ecommerceapp.pojo.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class HomeViewModel extends AndroidViewModel {

    private static final String TAG = "HomeViewModel";

    private final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
            .child("Profile Images");

    private final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("Users");
    private final DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("Products");
    private final DatabaseReference rootCartReference = FirebaseDatabase.getInstance().getReference("Cart List");

    private DatabaseReference cartProductsReference;
    private DatabaseReference cartReference;
    private DatabaseReference ordersReference;

    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Product>> productMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Cart>> cartProductsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Category>> categoryMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Product>> categoryProductsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Order>> ordersMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Task<Void>> shipmentTaskMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Task<Void>> addItemToCartTaskMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Task<Void>> updateProductTaskMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Task<Void>> deleteProductTaskMutableLiveData = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        getProducts();

        if (auth.getCurrentUser()!=null) {
            getUserData();
            cartProductsReference = FirebaseDatabase.getInstance()
                    .getReference("Cart List")
                    .child("User View")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .child("Products");

             cartReference = FirebaseDatabase.getInstance().getReference("Cart List")
                    .child("User View").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            ordersReference = FirebaseDatabase.getInstance().getReference("Orders")
                    .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        }
    }


    public void getUserData(){
        DatabaseReference ref = userReference
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                userMutableLiveData.postValue(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

    }

    public void updateUserInfo(String username, String phoneNumber, String address){
        HashMap<String, Object> userInfo = new HashMap<>();

        userInfo.put("name", Objects.requireNonNull(username));
        userInfo.put("phone_number", Objects.requireNonNull(phoneNumber));
        userInfo.put("address", Objects.requireNonNull(address));

        userReference.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .updateChildren(userInfo).addOnCompleteListener(task -> {
            if (!task.isSuccessful()){
                Log.d(TAG, "updateUserInfo: Successful");
            }else {
                Log.d(TAG, "updateUserInfo: "+task.getResult());
            }
        });
    }

    public void uploadImage(Uri fileUri){

        StorageReference filePath = storageReference
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+".jpg");

        final UploadTask uploadTask = filePath.putFile(fileUri);

        uploadTask.addOnFailureListener(e ->
                Log.d(TAG, "uploadImage: "+ e.getMessage()))
                .addOnSuccessListener(taskSnapshot -> uploadTask.continueWithTask(task -> {

                    if (!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }

                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUrl = Objects.requireNonNull(task.getResult()).toString();
                        HashMap<String, Object> imageMap = new HashMap<>();
                        imageMap.put("image_url", imageUrl);
                        userReference.child(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                                .updateChildren(imageMap).addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()){
                                Log.d(TAG, "uploadImage: "+
                                        Objects.requireNonNull(task1.getException()).getMessage());
                            }
                        });
                    }
                }));
    }

    public void getProducts(){
        ArrayList<Product> products = new ArrayList<>();

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    assert product != null;
                    products.add(product);
                }

                productMutableLiveData.postValue(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    public void getCartProducts(){

        ArrayList<Cart> products = new ArrayList<>();

        cartProductsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                products.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Cart product = dataSnapshot.getValue(Cart.class);

                    assert product != null;
                    products.add(product);
                }

                cartProductsMutableLiveData.postValue(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    public void removeProductFromTheCart(String pid){

        DatabaseReference userReference = FirebaseDatabase.getInstance()
                .getReference("Cart List").child("User View")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Products")
                .child(pid);

        DatabaseReference adminReference = FirebaseDatabase.getInstance()
                .getReference("Cart List").child("Admin View")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products")
                .child(pid);

        userReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                adminReference.removeValue().addOnCompleteListener(task1 ->{
                    if (task1.isSuccessful()){
                        Log.d(TAG, "removeProductFromTheCart: Successful");
                    }else {
                        Log.d(TAG, "removeProductFromTheCart: "+
                                Objects.requireNonNull(task1.getException()).getMessage());
                    }
                });
            }
        });

    }

    public void getCategories(){

        ArrayList<Category> categories = new ArrayList<>();

        categories.add(new Category(getApplication().getString(R.string.t_shirts), R.drawable.tshirts_category));
        categories.add(new Category(getApplication().getString(R.string.sports_clothes), R.drawable.sports_category));
        categories.add(new Category(getApplication().getString(R.string.female_clothes), R.drawable.female_category));
        categories.add(new Category(getApplication().getString(R.string.jackets), R.drawable.jackets_category));
        categories.add(new Category(getApplication().getString(R.string.glasses), R.drawable.glasses_category));
        categories.add(new Category(getApplication().getString(R.string.hats), R.drawable.hats_category));
        categories.add(new Category(getApplication().getString(R.string.bags), R.drawable.bags_category));
        categories.add(new Category(getApplication().getString(R.string.shoes), R.drawable.shoes_category));
        categories.add(new Category(getApplication().getString(R.string.headphones), R.drawable.headphones_category));
        categories.add(new Category(getApplication().getString(R.string.laptops), R.drawable.laptops_category));
        categories.add(new Category(getApplication().getString(R.string.accessories), R.drawable.accessories_category));
        categories.add(new Category(getApplication().getString(R.string.phones), R.drawable.phones_category));

        categoryMutableLiveData.postValue(categories);
    }

    public void getCategoryProducts(String categoryName){

        ArrayList<Product> products = new ArrayList<>();

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    assert product != null;
                    if (categoryName.equals(product.getCategory())) {
                        products.add(product);
                    }
                }

                categoryProductsMutableLiveData.postValue(products);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    public void getOrders(){

        ArrayList<Order> orders = new ArrayList<>();

        ordersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Order order = dataSnapshot.getValue(Order.class);
                        assert order != null;
                        orders.add(order);
                    }

                    ordersMutableLiveData.postValue(orders);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    public void decreaseProductsQuantity(ArrayList<Cart> products){

        for (Cart product: products){

            int finalQuantity = Integer.parseInt(product.getTotal_pieces()) -
                    Integer.parseInt(product.getQuantity());

            DatabaseReference productReference = FirebaseDatabase.getInstance().getReference("Products")
                    .child(product.getPid());

            if (finalQuantity>0) {
                HashMap<String, Object> productMap = new HashMap<>();
                productMap.put("pieces", String.valueOf(finalQuantity));

                productReference.updateChildren(productMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: Successful");
                    }else {
                        Log.d(TAG, "onComplete: "+ Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
            }else {
                productReference.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Log.d(TAG, "onComplete: Successful");
                    }else {
                        Log.d(TAG, "onComplete: "+ Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
            }
        }
    }

    public void confirmShipment(ArrayList<Cart> products, Order order){

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());

        String transactionId = String.valueOf(System.currentTimeMillis());

        String allProducts = "";
        for (Cart product: products){
            allProducts += product.getQuantity()+" "+product.getName()+" | ";
        }

        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("address", Objects.requireNonNull(order.getAddress()));
        orderMap.put("city", Objects.requireNonNull(order.getCity()));
        orderMap.put("date", saveCurrentDate);
        orderMap.put("name", Objects.requireNonNull(order.getName()));
        orderMap.put("phone", Objects.requireNonNull(order.getPhone()));
        orderMap.put("state", "Not Shipped");
        orderMap.put("time", saveCurrentTime);
        orderMap.put("total_price", order.getTotal_price());
        orderMap.put("products", allProducts);
        orderMap.put("transaction_id", transactionId);
        orderMap.put("uid", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

        ordersReference.child(transactionId).setValue(orderMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                cartReference.removeValue().addOnCompleteListener(task1 ->
                        shipmentTaskMutableLiveData.postValue(task1));
            }
        });

        decreaseProductsQuantity(products);
    }

    public void addItemToCartList(Product product, String totalPrice, String quantity){

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());
        String saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("date", saveCurrentDate);
        productMap.put("discount", "");
        productMap.put("pid", product.getPid());
        productMap.put("name", product.getProduct_name());
        productMap.put("price", String.valueOf(totalPrice));
        productMap.put("quantity", String.valueOf(quantity));
        productMap.put("time", saveCurrentTime);
        productMap.put("image_url", product.getImage_url());
        productMap.put("total_pieces", product.getPieces());

        rootCartReference.child("Admin View")
                .child(Objects.requireNonNull(auth.getCurrentUser()).getUid()).child("Products")
                .child(product.getPid())
                .setValue(productMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                rootCartReference.child("User View").child(auth.getCurrentUser().getUid()).child("Products")
                        .child(product.getPid())
                        .setValue(productMap).addOnCompleteListener(task1 ->
                        addItemToCartTaskMutableLiveData.postValue(task1));
            }
        });

    }

    public void updateProduct(Product product, HashMap<String, Object> productMap){

        productReference.child(product.getPid()).updateChildren(productMap).addOnCompleteListener(task ->
                updateProductTaskMutableLiveData.postValue(task));
    }

    public void deleteProduct(Product product) {

        productReference.child(product.getPid()).removeValue().addOnCompleteListener(task ->
                deleteProductTaskMutableLiveData.postValue(task));
    }
}
