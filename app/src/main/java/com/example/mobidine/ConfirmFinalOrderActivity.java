package com.example.mobidine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mobidine.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ConfirmFinalOrderActivity extends AppCompatActivity {


    private EditText nameEditText, phoneEditText, addressEditText, cityEditText;
    private Button confirmOrderBtn;

    private String totalAmount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);

        totalAmount = getIntent().getStringExtra("Total Price");
        Toast.makeText(this, "Total Price:"+totalAmount+"LKR", Toast.LENGTH_SHORT).show();

        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.delivery_name);
        phoneEditText = (EditText) findViewById(R.id.delivery_phone);
        addressEditText = (EditText) findViewById(R.id.delivery_address);
        cityEditText = (EditText) findViewById(R.id.delivery_city);



        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //form validation
                Check();

            }
        });


    }

    private void Check() {


        if(TextUtils.isEmpty(nameEditText.getText().toString())){

            Toast.makeText(this, "Please provide you name", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(phoneEditText.getText().toString())){

            Toast.makeText(this, "Please provide you phone number", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(addressEditText.getText().toString())){

            Toast.makeText(this, "Please provide you address", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(cityEditText.getText().toString())){

            Toast.makeText(this, "Please provide you city", Toast.LENGTH_SHORT).show();

        }else{

            confirmOrder();

        }

    }

    private void confirmOrder(){

        final String saveCurrentTime, saveCurrentDate;
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());


        final DatabaseReference orderRef = FirebaseDatabase.getInstance()
                .getReference().child("Orders").child(Prevalent.currentonlineUser.getPhone());

        HashMap<String, Object> ordersMap = new HashMap<>();

        ordersMap.put("totalAmount", totalAmount);
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());
        ordersMap.put("city", cityEditText.getText().toString());
        ordersMap.put("date", saveCurrentDate);
        ordersMap.put("time", saveCurrentTime);
        ordersMap.put("state", "not delivered");


        orderRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentonlineUser.getPhone())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(ConfirmFinalOrderActivity.this, "Your Order Placed Successfully...", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                //once placed the order user can't come back
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                startActivity(intent);
                                finish();
                            }


                        }
                    });

                }

            }
        });

    }


}
