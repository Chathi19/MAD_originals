package com.example.music_store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music_store.model.request_rent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class sell_request_screen extends AppCompatActivity {


    TextView instrumentId, coustomerId;
    EditText contact_num, register_dt, num_days;
    Button request;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_request_screen);

        instrumentId = findViewById(R.id.textView11);
        coustomerId = findViewById(R.id.textView14);
        contact_num = findViewById(R.id.editTextTextPersonName);
        register_dt = findViewById(R.id.editTextTextPersonName2);
        num_days = findViewById(R.id.editTextTextPersonName3);
        request = findViewById(R.id.button3);

        db = FirebaseFirestore.getInstance();


        Bundle extra = getIntent().getExtras();

        instrumentId.setText(extra.getString("itemId"));
        coustomerId.setText(extra.getString("coustomer"));


        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String instymentIds = instrumentId.getText().toString();
                String clientId = coustomerId.getText().toString();
                String contact_nums = contact_num.getText().toString();
                String reqDays = register_dt.getText().toString();
                String numDays = num_days.getText().toString();

                System.out.println("data "+instymentIds+" "+clientId+" "+contact_nums+" "+reqDays+" "+numDays);

                request_rent itm = new request_rent();

                itm.setClientId(clientId);
                itm.setInsturmentId(instymentIds);
                itm.setContactNum(contact_nums);
                itm.setReq_date(reqDays);
                itm.setNum_days(numDays);


                addRentRequest(itm);

            }
        });
    }


    public void addRentRequest(request_rent item){

        ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        CollectionReference dbRentItem = db.collection("request_sell_item");

        dbRentItem.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();
                Toast.makeText(sell_request_screen.this, "Your request has been added to Firebase Firestore", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(sell_request_screen.this,MainActivity.class);
                sell_request_screen.this.startActivity(i);
                sell_request_screen.this.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(sell_request_screen.this, "Fail to add request \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }
}