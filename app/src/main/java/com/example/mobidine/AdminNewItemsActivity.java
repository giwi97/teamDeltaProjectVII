package com.example.mobidine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminNewItemsActivity extends AppCompatActivity {

    private String categoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private ImageView InputProductImage;
    private Button addNewProductButton;
    private EditText InputProductName, InputProductDescription, InputProductPrice;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private String productRandomKey;
    private StorageReference ProductImageRef;
    private String downloadImageUrl;
    private DatabaseReference ProductsRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_new_items);

        categoryName = getIntent().getExtras().get("Category").toString();
        ProductImageRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        InputProductImage = (ImageView) findViewById(R.id.select_food_image);
        addNewProductButton = (Button) findViewById(R.id.add_new_product);
        InputProductName = (EditText) findViewById(R.id.product_name);
        InputProductDescription = (EditText) findViewById(R.id.product_description);
        InputProductPrice = (EditText) findViewById(R.id.product_price);
        loadingBar = new ProgressDialog(this);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGallery();

            }
        });


        addNewProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateProductData();

            }
        });

    }


    private void openGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GalleryPick && resultCode == RESULT_OK && data != null){

            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);

        }

    }

    private void validateProductData() {

        Description = InputProductDescription.getText().toString();
        Price = InputProductPrice.getText().toString();
        Pname = InputProductName.getText().toString();

        if(ImageUri == null){

            Toast.makeText(this, "Please select image for product", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Description)){

            Toast.makeText(this, "Please enter product description", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(Price)){

            Toast.makeText(this, "Please enter product price", Toast.LENGTH_SHORT).show();

        }else if (TextUtils.isEmpty(Pname)){

            Toast.makeText(this, "Please enter product name", Toast.LENGTH_SHORT).show();

        }else {

            StoreProductInformation();
        }

    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Add new product");
        loadingBar.setMessage("Please wait..");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calender = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calender.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calender.getTime());
        productRandomKey = saveCurrentDate + saveCurrentTime;

        final StorageReference filePath = ProductImageRef.child(ImageUri.getLastPathSegment() +productRandomKey+".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                String message = e.toString();
                Toast.makeText(AdminNewItemsActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(AdminNewItemsActivity.this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if(!task.isSuccessful()){

                            throw task.getException();

                        }



                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){

                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AdminNewItemsActivity.this, "Product image URL taken", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();

                        }

                    }
                });

            }
        });

    }

    private void saveProductInfoToDatabase() {

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("description", Description);
        productMap.put("image", downloadImageUrl);
        productMap.put("category", categoryName);
        productMap.put("price", Price);
        productMap.put("name", Pname);

        ProductsRef.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Intent intent = new Intent(AdminNewItemsActivity.this, AdminCategoryActivity.class);
                    startActivity(intent);

                    loadingBar.dismiss();
                    Toast.makeText(AdminNewItemsActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();

                }else {
                    loadingBar.dismiss();
                    String msg = task.getException().toString();
                    Toast.makeText(AdminNewItemsActivity.this, "Error occured"+msg, Toast.LENGTH_SHORT).show();

                }

            }
        });


    }
}
