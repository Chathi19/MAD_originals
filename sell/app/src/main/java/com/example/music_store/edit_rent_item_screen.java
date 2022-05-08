package com.example.music_store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.music_store.model.rentItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class edit_rent_item_screen extends AppCompatActivity {

    Button editButton;
    EditText edit_productName;
    EditText edit_avalibility;
    EditText edit_price_per_day;
    EditText edit_description;
    ImageView edit_uploadImage;
    Uri uri;

    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageReference;

    String Item_ID = null;


    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rent_item_screen);

        editButton = findViewById(R.id.button);
        edit_productName = findViewById(R.id.editTextText1);
        edit_avalibility = findViewById(R.id.editTextText2);
        edit_price_per_day = findViewById(R.id.editTextText3);
        edit_description = findViewById(R.id.editTextText4);
        edit_uploadImage = findViewById(R.id.imageView3);


        progressDialog = new ProgressDialog(this);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseFirestore.getInstance();

        Bundle extra = getIntent().getExtras();

        Item_ID = extra.getString("itemId");

        edit_productName.setText(extra.getString("itemName"));
        edit_avalibility.setText(extra.getString("qty"));
        edit_price_per_day.setText(extra.getString("price_per"));
        edit_description.setText(extra.getString("desciption"));

        Picasso
                .get()
                .load(extra.getString("itemImg"))
                .into(edit_uploadImage);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String item_name = edit_productName.getText().toString();
                String item_qty = edit_avalibility.getText().toString();
                String price = edit_price_per_day.getText().toString();
                String desciptions = edit_description.getText().toString();


                System.out.println("data "+item_name+" "+item_qty+" "+price+" "+desciptions);

                rentItem itm =  new rentItem();
                itm.setRentItemId(UUID.randomUUID().toString());
                itm.setItemName(item_name);
                itm.setPrice_pre_day(price);
                itm.setDescription(desciptions);
                itm.setAvalability(item_qty);



                if(uri != null){
                    addDataToServer(itm);
                }else {
                    //set defulat url
                    itm.setImage_data(extra.getString("itemImg"));
                    updateDatas(itm,progressDialog);


                }
            }
        });

        edit_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageFromGallery();
            }
        });
    }


    public void selectImageFromGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && data != null && data.getData() != null) {
            uri = data.getData();
            edit_uploadImage.setImageURI(null);
            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                uri);
                edit_uploadImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //uploadImage.setImageURI(uri);
        }
    }


    public void addDataToServer(rentItem item){

        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        String file_id = UUID.randomUUID().toString();

        StorageReference ref
                = storageReference
                .child(
                        "images/"
                                + file_id);

        item.setImage_data("images/"+file_id);

        ref.putFile(uri)
                .addOnSuccessListener(
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(
                                    UploadTask.TaskSnapshot taskSnapshot)
                            {


                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        System.out.println("final url "+String.valueOf(uri));
                                        item.setImage_data(String.valueOf(uri));

                                        //System.out.println("## Stored path iss "+ref.getDownloadUrl().toString());
                                        updateDatas(item,progressDialog);
                                        // DatabaseReference imgStore = FirebaseDatabase.getInstance().getReference().child("images");
                                    }
                                });

                                // Image uploaded successfully
                                // Dismiss dialog
                                //System.out.println("## Stored path iss "+ref.getDownloadUrl().toString());
                                //updateDatas(item,taskSnapshot,progressDialog);
                                //addDatas(item,taskSnapshot,progressDialog);


                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(edit_rent_item_screen.this,
                                        "Failed " + e.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .addOnProgressListener(
                        new OnProgressListener<UploadTask.TaskSnapshot>() {

                            // Progress Listener for loading
                            // percentage on the dialog box
                            @Override
                            public void onProgress(
                                    UploadTask.TaskSnapshot taskSnapshot)
                            {
                                double progress
                                        = (100.0
                                        * taskSnapshot.getBytesTransferred()
                                        / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage(
                                        "Uploaded "
                                                + (int)progress + "%");
                            }
                        });

    }

    public void updateDatas(rentItem updateItem, ProgressDialog progressDialog){


        System.out.println("start added data");


        db.collection("rent_item").
                // below line is use toset the id of
                // document where we have to perform
                // update operation.
                        document(
                        Item_ID). //courses.getId()

                // after setting our document id we are
                // passing our whole object class to it.
                        set(updateItem).

                // after passing our object class we are
                // calling a method for on success listener.
                        addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // on successful completion of this process
                        // we are displaying the toast message.
                        progressDialog.dismiss();
                        Toast.makeText(edit_rent_item_screen.this, "Course has been updated..", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {


            // inside on failure method we are
            // displaying a failure message.
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(edit_rent_item_screen.this, "Fail to update the data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

}