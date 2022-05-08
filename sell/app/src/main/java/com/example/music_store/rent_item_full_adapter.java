package com.example.music_store;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_store.model.rentItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class rent_item_full_adapter extends RecyclerView.Adapter<rent_item_full_adapter.ViewHolder> {

    private ArrayList<rentItem> itemArrayList;
    private Context context;

    FirebaseFirestore db;

    public rent_item_full_adapter(ArrayList<rentItem> itemArrayList, Context context) {
        this.itemArrayList = itemArrayList;
        this.context = context;
        db = FirebaseFirestore.getInstance();
    }

//-------------------------------------------------------------------------------
    @NonNull
    @Override
    public rent_item_full_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new rent_item_full_adapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.single_rent_item_details_fragment, parent, false));
    }



    @Override
    public void onBindViewHolder(@NonNull rent_item_full_adapter.ViewHolder holder, int position) {
        rentItem item = itemArrayList.get(position);

        Picasso
                .get()
                .load(item.getImage_data())
                .into(holder.imege);

        //holder.imege.setImageURI(Uri.parse(item.getImage_data()));
        holder.itemName.setText(item.getItemName());
        holder.itemQty.setText(item.getAvalability());
        holder.itemPrice.setText(item.getPrice_pre_day());
        holder.itemDes.setText(item.getDescription());
        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //-----------------------------------------------------------------------need to get item doc id and set into item id------------------------------------------
                System.out.println("item "+item.getRentItemId());

                Intent i = new Intent(context,edit_rent_item_screen.class);
                i.putExtra("itemName",item.getItemName());
                i.putExtra("itemId",item.getRentItemId());
                i.putExtra("itemImg",item.getImage_data());
                i.putExtra("price_per",item.getPrice_pre_day());
                i.putExtra("qty",item.getAvalability());
                i.putExtra("desciption",item.getDescription());
                view.getContext().startActivity(i);





                //context.getApplicationContext().startActivity(i);
                ///MainActivity.this.finish();

            }
        });

        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //need to get item doc id and set into item id------------------------------
                    System.out.println("item "+item.getRentItemId());

                     ProgressDialog progressDialog
                        = new ProgressDialog(context);
                   progressDialog.setTitle("deleteing data...");
                   progressDialog.show();


                   db.collection("rent_item").
                                document(item.getRentItemId()).

                                delete().
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(view.getContext(), "rent item has been deleted from Database.", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(view.getContext(), MainActivity.class);
                                    view.getContext().startActivity(i);
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(view.getContext(), "Fail to delete the rent item. ", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imege;
        TextView itemName,itemQty,itemPrice,itemDes;
        Button buttonEdit,buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imege = itemView.findViewById(R.id.imageView2);
            itemName = itemView.findViewById(R.id.textView8);
            itemQty = itemView.findViewById(R.id.textView9);
            itemPrice = itemView.findViewById(R.id.textView12);
            itemDes = itemView.findViewById(R.id.textView19);
            buttonEdit = itemView.findViewById(R.id.button4);
            buttonDelete = itemView.findViewById(R.id.button5);
        }
    }
}
