package com.example.cartmanagement.adapter;

import static java.lang.Float.parseFloat;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cartmanagement.R;
import com.example.cartmanagement.eventbus.MyUpdateCartEvent;
import com.example.cartmanagement.listener.ICartLoadListener;
import com.example.cartmanagement.listener.IRecyclerViewClickListener;
import com.example.cartmanagement.model.CartModel;
import com.example.cartmanagement.model.ItemModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.nio.file.WatchEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyItemAdapter extends RecyclerView.Adapter<MyItemAdapter.MyItemViewHolder> {

    private Context context;
    private final List<ItemModel> itemModelList;
    private ICartLoadListener iCartLoadListener;

    public MyItemAdapter(Context context, List<ItemModel> itemModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.itemModelList = itemModelList;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyItemViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_item_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyItemViewHolder holder, int position) {

        Glide.with(context)
                .load(itemModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("LKR ").append(itemModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(itemModelList.get(position).getName()));

        holder.setListener((view, adapterPosition) -> {
            addToCart(itemModelList.get(position));

        });
    }

    private void addToCart(ItemModel itemModel) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");
        userCart.child(itemModel.getKey());
        userCart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()) {

                    CartModel cartModel = snapshot.getValue(CartModel.class);
                    cartModel.setQuantity(cartModel.getQuantity()+1);
                    Map<String,Object> updateData = new HashMap<>();
                    updateData.put("quantity", cartModel.getQuantity());
                   //updateData.put("totalPrice", cartModel.getQuantity()*Float.par(cartModel.getPrice()));
                    userCart.child(itemModel.getKey())
                            .updateChildren(updateData)
                            .addOnSuccessListener(aVoid -> {
                                iCartLoadListener.onCartLoadFailed("Add to Cart");
                            })
                            .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                } else {
                    CartModel cartModel = new CartModel();
                    cartModel.setName(itemModel.getName());
                    cartModel.setImage(itemModel.getImage());
                    cartModel.setKey(itemModel.getKey());
                    cartModel.setPrice(itemModel.getPrice());
                    cartModel.setQuantity(1);
                  cartModel.setTotalPrice(Float.parseFloat(itemModel.getPrice()));

                    userCart.child(itemModel.getKey())
                            .setValue(cartModel)
                            .addOnSuccessListener(aVoid -> {
                                iCartLoadListener.onCartLoadFailed("Add to Cart");
                            })
                            .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));
                }

                EventBus.getDefault().postSticky(new MyUpdateCartEvent());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                iCartLoadListener.onCartLoadFailed(error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemModelList.size();
    }

    public class MyItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
@BindView(R.id.imageView)
    ImageView imageView;
@BindView(R.id.txtName)
    TextView txtName;
@BindView(R.id.txtPrice)
TextView txtPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;
    public MyItemViewHolder(@NonNull View itemView) {
        super(itemView);

        unbinder = ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(this);
    }

        @Override
        public void onClick(View v){

                listener.onRecycleClick(v,getAdapterPosition());


            }


        }
    }
