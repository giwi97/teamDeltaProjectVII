package com.example.mobidine;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

public class AdminNewItemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_items);

        Toast.makeText(this, "Welcome admin!", Toast.LENGTH_SHORT).show();

    }
}
