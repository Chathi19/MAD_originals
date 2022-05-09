package com.example.cartmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.cartmanagement.adapter.MyItemAdapter;
import com.example.cartmanagement.eventbus.MyUpdateCartEvent;
import com.example.cartmanagement.listener.ICartLoadListener;
import com.example.cartmanagement.listener.ItemLoadListener;
import com.example.cartmanagement.model.CartModel;
import com.example.cartmanagement.model.ItemModel;
import com.example.cartmanagement.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ItemLoadListener, ICartLoadListener {

    @BindView(R.id.recycler_item)
    RecyclerView recyclerItem;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;


    ItemLoadListener itemLoadListener;
    ICartLoadListener cartLoadListener;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event){

        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        init();
        loadItemFromFirebase();
        countCartItem();
    }

    private void loadItemFromFirebase() {
        List<ItemModel> itemModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Instrument")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){

                            for(DataSnapshot itemSnapshot:snapshot.getChildren()){
                                ItemModel itemModel = itemSnapshot.getValue(ItemModel.class);
                                itemModel.setKey(itemSnapshot.getKey());
                                itemModels.add(itemModel);
                            }
                            itemLoadListener.onItemLoadSuccess(itemModels);
                        }
                        else
                            itemLoadListener.onItemLoadFailed("Can't find Instrument");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
itemLoadListener.onItemLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){

        ButterKnife.bind(this);

        itemLoadListener = this;
        cartLoadListener = this;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerItem.setLayoutManager(gridLayoutManager);
        recyclerItem.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v -> startActivity(new Intent(this,CartActivity.class)));
    }

    @Override
    public void onItemLoadSuccess(List<ItemModel> itemModelList) {
MyItemAdapter adapter = new MyItemAdapter( this,itemModelList,cartLoadListener);
recyclerItem.setAdapter(adapter);
    }

    @Override
    public void onItemLoadFailed(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {
        int cartSum = 0;
        for(CartModel cartModel: cartModelList)
            cartSum +=cartModel.getQuantity();
        badge.setNumber(cartSum);
    }

    @Override
    public void onCartLoadFailed(String message) {
            Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("UNIQUE_USER_ID")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot cartSnapshop:snapshot.getChildren()){
                            CartModel cartModel =  cartSnapshop.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshop.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }
}