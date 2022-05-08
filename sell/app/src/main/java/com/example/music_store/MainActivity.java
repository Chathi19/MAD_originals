package com.example.music_store;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button add_item, edit_item, request_rent, view_rentList,admin_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_item = findViewById(R.id.button6);
        edit_item = findViewById(R.id.button7);
        request_rent = findViewById(R.id.button8);
        view_rentList = findViewById(R.id.button9);
        admin_List = findViewById(R.id.button10);


        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, sell_item_add_screen.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        });

        edit_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, edit_sell_item_screen.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        });

        request_rent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, sell_request_screen.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        });

        view_rentList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, sell_list_screen.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        });

        admin_List.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, admin_sell_list_view.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        });
    }


}