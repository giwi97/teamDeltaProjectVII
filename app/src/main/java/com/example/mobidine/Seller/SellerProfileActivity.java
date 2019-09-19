package com.example.mobidine.Seller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobidine.MainActivity;
import com.example.mobidine.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

public class SellerProfileActivity extends AppCompatActivity {

    private ImageView restaurantImageView;
    private TextView closeBtn, updateBtn, changePropicBtn;
    private Button deactivateSellerBtn, logoutSellerBtn;
    private EditText sellerName, sellerNumber, sellerAddress;


    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private String checker = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);


        storageReference = FirebaseStorage.getInstance().getReference().child("Restaurant pictures");
        
        
        sellerName = findViewById(R.id.seller_settings_name);
        sellerNumber = findViewById(R.id.seller_settings_phone_number);
        sellerAddress = findViewById(R.id.seller_settings_address);

        restaurantImageView = findViewById(R.id.settings_seller_profile_image);
        closeBtn = findViewById(R.id.close_seller_settings_btn);
        updateBtn = findViewById(R.id.update_seller_account_settings_btn);
        changePropicBtn = findViewById(R.id.seller_profile_image_change_btn);

        deactivateSellerBtn = findViewById(R.id.deactivate_seller);
        logoutSellerBtn = findViewById(R.id.seller_logout_btn);



        sellerInfoDisplay(restaurantImageView, sellerName, sellerNumber, sellerAddress);
        
        

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();


            }
        });

        
        logoutSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    final FirebaseAuth mAuth;

                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();

                    Intent intent = new Intent(SellerProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();


            }
        });

    }

    private void sellerInfoDisplay(ImageView restaurantImageView, EditText sellerName, EditText sellerNumber, EditText sellerAddress) {
    }
}
