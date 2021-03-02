package com.shorbgy.ecommerceapp.ui.home_activity.fragments;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.shorbgy.ecommerceapp.R;
import com.shorbgy.ecommerceapp.adapters.CartAdapter;
import com.shorbgy.ecommerceapp.databinding.FragmentCartBinding;
import com.shorbgy.ecommerceapp.pojo.Cart;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeActivity;
import com.shorbgy.ecommerceapp.ui.home_activity.HomeViewModel;
import com.shorbgy.ecommerceapp.utils.Constants;
import java.util.ArrayList;
import java.util.Objects;

public class CartFragment extends Fragment {

    private HomeViewModel viewModel;
    private CartAdapter adapter;
    private FragmentCartBinding binding;

    private int totalPrice;
    private ArrayList<Cart> products = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.cart));

        viewModel = ((HomeActivity) requireActivity()).viewModel;
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false);
        adapter = new CartAdapter();

        getProducts();
        binding.productsRv.setAdapter(adapter);
        swipeToDelete();

        binding.buyNowButton.setOnClickListener(v -> {
            if (totalPrice>0){

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList(Constants.PRODUCTS_IN_CART, products);
                bundle.putInt(Constants.TOTAL_PRICE, totalPrice);

                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_cartFragment_to_shipmentFragment, bundle);
            }
        });

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    private void getProducts(){

        viewModel.getCartProducts();

        viewModel.cartProductsMutableLiveData.observe(getViewLifecycleOwner(), carts -> {
            totalPrice = 0;
            products = carts;
            for (Cart product: products){
                totalPrice += Integer.parseInt(product.getPrice());
            }
            adapter.setProducts(products);
            adapter.notifyDataSetChanged();
            binding.totalPrice.setText("$"+ totalPrice);
        });
    }

    private void deleteProduct(String pid){
        viewModel.removeProductFromTheCart(pid);
    }

    private void swipeToDelete(){
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Cart cart = products.get(viewHolder.getAdapterPosition());
                deleteProduct(cart.getPid());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(binding.productsRv);
    }
}