package com.example.music_store;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.music_store.model.rentItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class rent_item_add_screen extends AppCompatActivity {


    //crete variables from all element classes
    Button addButton;
    EditText productName;
    EditText avalibility;
    EditText price_per_day;
    EditText description;
    ImageView uploadImage;
    Uri uri;


    //crete references
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rent_item_add_screen);


        //------------------------------------------------------------get the elemnts when load the screen
        addButton = findViewById(R.id.button);
        productName = findViewById(R.id.editTextText1);
        avalibility = findViewById(R.id.editTextText2);
        price_per_day = findViewById(R.id.editTextText3);
        description = findViewById(R.id.editTextText4);
        uploadImage = findViewById(R.id.imageView4);

        //----------------------------------------------------------get references
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("rent_item");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        db = FirebaseFirestore.getInstance();


        //-------------------------------------------------------------------------------
                 uploadImage.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                selectImageFromGallery();
            }
                  });


                  addButton.setOnClickListener(new View.OnClickListener() {
                      @Override
                       public void onClick(View v) {
                       String item_name  =  productName.getText().toString();
                       String item_qty   = avalibility.getText().toString();
                       String price      = price_per_day.getText().toString();
                       String desciptions = description.getText().toString();

                    //need to get image-------------------------------------------------------------------------

                       System.out.println("data "+item_name+" "+item_qty+" "+price+" "+desciptions);


                       rentItem itm =  new rentItem();  //--creating new instance and save-------------------------


                       itm.setRentItemId(UUID.randomUUID().toString());
                       itm.setItemName(item_name);
                       itm.setPrice_pre_day(price);
                       itm.setDescription(desciptions);

                        itm.setAvalability(item_qty);

                    addDataToServer(itm);


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
            uploadImage.setImageURI(null);


            try {
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                uri);
                uploadImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //uploadImage.setImageURI(uri);



        }
    }

    public void addDataToServer(rentItem item){




        ProgressDialog progressDialog
                = new ProgressDialog(this);
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
                                        saveDatas(item,progressDialog);
                                       // DatabaseReference imgStore = FirebaseDatabase.getInstance().getReference().child("images");
                                    }
                                });


                                // Image uploaded successfully-------------------------------------
                                // Dismiss dialog---------------------------------------------------

                                //addDatas(item,taskSnapshot,progressDialog);-----------------------


                            }
                        })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {

                        // Error, Image not uploaded
                        progressDialog.dismiss();
                        Toast
                                .makeText(rent_item_add_screen.this,
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



    public void addDatas(rentItem item, UploadTask.TaskSnapshot taskSnapshot, ProgressDialog progressDialog){
        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();

        // String generatedFilePath = downloadUri.getResult().toString();
        // System.out.println("## Stored path is "+generatedFilePath);

        if(downloadUri.isSuccessful()){
            String generatedFilePath = downloadUri.getResult().toString();
            System.out.println("## Stored path is "+generatedFilePath);

            item.setImage_data(generatedFilePath);


        }else{
            item.setImage_data("no url");
            progressDialog.dismiss();
            Toast
                    .makeText(rent_item_add_screen.this,
                            "Image Uploaded & Fail to add data !!",
                            Toast.LENGTH_SHORT)
                    .show();
        }

        System.out.println("start added data");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(item);
                Toast.makeText(rent_item_add_screen.this, "data added", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                Toast
//                        .makeText(rent_item_add_screen.this,
//                                "Image Uploaded & data added!!",
//                                Toast.LENGTH_SHORT)
//                        .show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(rent_item_add_screen.this, "Fail to add data ! " + error, Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//                Toast
//                        .makeText(rent_item_add_screen.this,
//                                "Image Uploaded & Fail to add data !!",
//                                Toast.LENGTH_SHORT)
//                        .show();
            }
        });
    }



    public void saveDatas(rentItem item,  ProgressDialog progressDialog){
        CollectionReference dbRentItem = db.collection("rent_item");

        System.out.println("start added data");


        dbRentItem.add(item).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressDialog.dismiss();
                Toast.makeText(rent_item_add_screen.this, "Your Data Has Been Added", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(rent_item_add_screen.this, "Fail to add to Database ! \n" + e, Toast.LENGTH_SHORT).show();
            }
        });

    }
}