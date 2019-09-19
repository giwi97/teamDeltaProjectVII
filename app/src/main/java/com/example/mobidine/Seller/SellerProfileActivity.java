package com.example.mobidine.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobidine.AdminMaintainProductsActivity;
import com.example.mobidine.HomeActivity;
import com.example.mobidine.MainActivity;
import com.example.mobidine.Prevalent.Prevalent;
import com.example.mobidine.R;
import com.example.mobidine.SettingsActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class SellerProfileActivity extends AppCompatActivity {

    private ImageView restaurantImageView;
    private TextView closeBtn, updateBtn, changePropicBtn;
    private Button deactivateSellerBtn, logoutSellerBtn;
    private EditText sellerName, sellerNumber, sellerAddress;
    private DatabaseReference sellerRef;


    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private String checker = "";
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);


        storageReference = FirebaseStorage.getInstance().getReference().child("Restaurant pictures");
        sellerRef = FirebaseDatabase.getInstance().getReference().child("Sellers");

        user = FirebaseAuth.getInstance().getCurrentUser();



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

        deactivateSellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deactivateSeller();

            }
        });



        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checker.equals("clicked")){

                    sellerInfoSaved();
                }else{

                    updateOnlySellerInfo();


                }

            }
        });


        changePropicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checker = "clicked";

                //Once user open the image gallery to select image, crop image window will open

                CropImage.activity(imageUri).setAspectRatio(5, 2)
                        .start(SellerProfileActivity.this);


            }
        });

    }

    private void deactivateSeller() {



        sellerRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(SellerProfileActivity.this, "Successfully delete", Toast.LENGTH_SHORT).show();


            }
        });




        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SellerProfileActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(SellerProfileActivity.this, "Account Deleted successfully", Toast.LENGTH_SHORT).show();;
                        }
                    }
                });



    }

    private void updateOnlySellerInfo() {


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

        HashMap<String, Object> sellerMap = new HashMap<>();
        sellerMap.put("name", sellerName.getText().toString());
        sellerMap.put("phone", sellerNumber.getText().toString());
        sellerMap.put("address", sellerAddress.getText().toString());

        ref.child(Prevalent.currentonlineUser.getPhone()).updateChildren(sellerMap);

        startActivity(new Intent(SellerProfileActivity.this, SellerHomeActivity.class));
        Toast.makeText(SellerProfileActivity.this, "Profile updated successfully..", Toast.LENGTH_SHORT).show();
        finish();


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            restaurantImageView.setImageURI(imageUri);
        }else{

            Toast.makeText(this, "Error occured!!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SellerProfileActivity.this, SellerProfileActivity.class));

            finish();

        }

    }


    private void sellerInfoSaved() {

        if(TextUtils.isEmpty(sellerName.getText().toString())){

            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(sellerNumber.getText().toString())){

            Toast.makeText(this, "Phone number is required", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(sellerAddress.getText().toString())){

            Toast.makeText(this, "Address is required", Toast.LENGTH_SHORT).show();

        }else if(checker.equals("clicked")){

            uploadImage();
        }

    }


    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please while, we're setting things..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri != null){

            final  StorageReference fileRef = storageReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()){

                        throw task.getException();

                    }



                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult();

                        myUrl = downloadUrl.toString();

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Sellers");

                        HashMap<String, Object> sellerMap = new HashMap<>();
                        sellerMap.put("name", sellerName.getText().toString());
                        sellerMap.put("phone", sellerNumber.getText().toString());
                        sellerMap.put("address", sellerAddress.getText().toString());
                        sellerMap.put("image", myUrl);

                        ref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(sellerMap);



                        progressDialog.dismiss();


                        startActivity(new Intent(SellerProfileActivity.this, SellerHomeActivity.class));
                        Toast.makeText(SellerProfileActivity.this, "Profile updated successfully..", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {

                        progressDialog.dismiss();
                        Toast.makeText(SellerProfileActivity.this, "Error occured", Toast.LENGTH_SHORT).show();


                    }

                }
            });

        }else {

            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();



        }

    }





    private void sellerInfoDisplay(final ImageView restaurantImageView, final EditText sellerName, final EditText sellerNumber, final EditText sellerAddress) {


        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists())){


                    if(dataSnapshot.child("image").exists()){

                        Log.d("Database error", "triggered");

                        String image = dataSnapshot.child("image").getValue().toString();
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();


                        Picasso.get().load(image).into(restaurantImageView);
                        sellerName.setText(name);
                        sellerNumber.setText(phone);
                        sellerAddress.setText(address);

                    }



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


                Log.d("Database error", databaseError.getDetails());


            }
        });

    }
}
