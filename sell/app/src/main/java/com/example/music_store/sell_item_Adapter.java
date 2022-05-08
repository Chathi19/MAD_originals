package com.example.music_store;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_store.model.rentItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class sell_item_Adapter extends RecyclerView.Adapter<sell_item_Adapter.ViewHolder> {

    private ArrayList<rentItem> itemArrayList;
    private Context context;


    public sell_item_Adapter(ArrayList<rentItem> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public sell_item_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_sell_item_fragment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull sell_item_Adapter.ViewHolder holder, int position) {
        rentItem item = itemArrayList.get(position);

        Picasso
                .get()
                .load(item.getImage_data())
                .into(holder.imege);


        holder.textView7.setText(item.getItemName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //need to get item doc id and set into item id
                System.out.println("item "+item.getRentItemId());
                Intent i = new Intent(context, sell_request_screen.class);
                i.putExtra("itemName",item.getItemName());
                i.putExtra("itemId",item.getRentItemId());
                i.putExtra("itemImg",item.getImage_data());
                i.putExtra("price_per",item.getPrice_pre_day());
                i.putExtra("qty",item.getAvalability());
                i.putExtra("desciption",item.getDescription());
                i.putExtra("coustomer","C0001");
                view.getContext().startActivity(i);

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imege;
        TextView textView7;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imege = itemView.findViewById(R.id.imageView);
            textView7 = itemView.findViewById(R.id.textView7);
            button = itemView.findViewById(R.id.button2);
        }
    }
}
