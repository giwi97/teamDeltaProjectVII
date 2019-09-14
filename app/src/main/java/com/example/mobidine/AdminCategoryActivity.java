package com.example.mobidine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AdminCategoryActivity extends AppCompatActivity {


    private ImageView burger, pizza, rice;
    private ImageView drinks, noodles, foods;


    private Button LogoutBtn, CheckOrdersBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_category);

        LogoutBtn = (Button) findViewById(R.id.admin_logout_btn);
        CheckOrdersBtn = (Button) findViewById(R.id.check_orders_btn);

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(AdminCategoryActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();


            }
        });


        CheckOrdersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewOrdersActivity.class);
                startActivity(intent);


            }
        });

        burger = (ImageView) findViewById(R.id.hamburger);
        pizza = (ImageView) findViewById(R.id.pizza);
        rice = (ImageView) findViewById(R.id.rice);
        drinks = (ImageView) findViewById(R.id.drinks);
        noodles = (ImageView) findViewById(R.id.noodles);
        foods = (ImageView) findViewById(R.id.food);

        burger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewItemsActivity.class);
                intent.putExtra("Category", "Burger");
                startActivity(intent);

            }
        });

        pizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewItemsActivity.class);
                intent.putExtra("Category", "Pizza");
                startActivity(intent);

            }
        });

        rice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewItemsActivity.class);
                intent.putExtra("Category", "Rice");
                startActivity(intent);

            }
        });

        drinks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewItemsActivity.class);
                intent.putExtra("Category", "Drinks");
                startActivity(intent);

            }
        });

        noodles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewItemsActivity.class);
                intent.putExtra("Category", "Noodles");
                startActivity(intent);
            }
        });

        foods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(AdminCategoryActivity.this, AdminNewItemsActivity.class);
                intent.putExtra("Category", "Others");
                startActivity(intent);

            }
        });

    }
}
