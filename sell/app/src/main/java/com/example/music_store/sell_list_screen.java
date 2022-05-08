package com.example.music_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.music_store.model.rentItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class sell_list_screen extends AppCompatActivity {


    RecyclerView itemListView;
    ArrayList<rentItem> rentItemArrayList;
    sell_item_Adapter item_adapter;
    FirebaseFirestore db;
    ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_list_screen);

        itemListView = findViewById(R.id.itemListView);
        db = FirebaseFirestore.getInstance();


        rentItemArrayList = new ArrayList<>();
        itemListView.setHasFixedSize(true);
        itemListView.setLayoutManager(new LinearLayoutManager(this));

        item_adapter = new sell_item_Adapter(rentItemArrayList, this);

        // setting adapter to our recycler view.
        itemListView.setAdapter(item_adapter);

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Fetching data...");
        progressDialog.show();

        db.collection("sell_item").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            System.out.println("data found");
                           // loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                System.out.println("data found id "+d.getId());
                                rentItem c = d.toObject(rentItem.class);
                                c.setRentItemId(d.getId());

                                System.out.println("data found des "+c.getDescription());

                                rentItemArrayList.add(c);
                            }
                            progressDialog.dismiss();
                            item_adapter.notifyDataSetChanged();
                        } else {
                            progressDialog.dismiss();
                            // if the snapshot is empty we are displaying a toast message.
                            Toast.makeText(sell_list_screen.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                progressDialog.dismiss();
                Toast.makeText(sell_list_screen.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}