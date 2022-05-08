package com.example.music_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.music_store.model.rentItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class admin_rent_list_view extends AppCompatActivity {

    RecyclerView itemListView;
    ArrayList<rentItem> rentItemArrayList;
    rent_item_full_adapter item_adapter;
    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_rent_list_view);

        itemListView = findViewById(R.id.admin_rent_list);
        db = FirebaseFirestore.getInstance();


        rentItemArrayList = new ArrayList<>();

        itemListView.setHasFixedSize(true);
        itemListView.setLayoutManager(new LinearLayoutManager(this));

        item_adapter = new rent_item_full_adapter(rentItemArrayList, this);

        // ----------------------------setting adapter to our recycler view.-------------------------------------------------------------
        itemListView.setAdapter(item_adapter);

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Fetching data...");
        progressDialog.show();

        db.collection("rent_item").get()
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
                            // if the snapshot is empty we are displaying a toast message.
                            progressDialog.dismiss();
                            Toast.makeText(admin_rent_list_view.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                progressDialog.dismiss();
                Toast.makeText(admin_rent_list_view.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}