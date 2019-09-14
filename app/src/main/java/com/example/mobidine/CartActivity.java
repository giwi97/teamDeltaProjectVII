package com.example.mobidine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobidine.Model.Cart;
import com.example.mobidine.Prevalent.Prevalent;
import com.example.mobidine.ViewHolder.CartViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private  RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount, txtMsg1;
    private int overTotalPrice = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        NextProcessBtn = (Button) findViewById(R.id.next_process_btn);
        txtTotalAmount = (TextView) findViewById(R.id.total_price);
        txtMsg1 = (TextView) findViewById(R.id.msg1);


        NextProcessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                txtTotalAmount.setText("Total Price:"+String.valueOf(overTotalPrice)+"LKR");

                Intent intent = new Intent(CartActivity.this, ConfirmFinalOrderActivity.class);
                intent.putExtra("Total Price", String.valueOf(overTotalPrice));
                startActivity(intent);
                finish();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        CheckOrderState();

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef.child("User View")
                        .child(Prevalent.currentonlineUser.getPhone()).child("Products"), Cart.class)
                        .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter =
                new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull final Cart cart) {

                cartViewHolder.txtProductQuantity.setText("Quantity"+cart.getQuantity());
                cartViewHolder.txtProductPrice.setText("Price"+cart.getPrice()+"LKR");
                cartViewHolder.txtProductName.setText(cart.getName());


                int oneProductTotalPrice = ((Integer.valueOf(cart.getPrice()))) * ((Integer.valueOf(cart.getQuantity())));
                overTotalPrice = overTotalPrice + oneProductTotalPrice;

                cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[] = new CharSequence[]{

                                    "Edit",
                                    "Remove"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options:");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(i == 0){

                                    Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid", cart.getPid());
                                    startActivity(intent);
                                }

                                if (i == 1){

                                   cartListRef.child("User View").child(Prevalent.currentonlineUser.getPhone())
                                           .child("Products").child(cart.getPid())
                                           .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           if (task.isSuccessful()){

                                               Toast.makeText(CartActivity.this, "Item Removed", Toast.LENGTH_SHORT).show();

                                               Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                                               startActivity(intent);
                                           }

                                       }
                                   });

                                }

                            }
                        });


                        builder.show();

                    }
                });

            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout, parent, false);
                CartViewHolder holder = new CartViewHolder(view);

                return holder;

            }
        };


        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void CheckOrderState(){

        DatabaseReference ordersRef;
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(Prevalent.currentonlineUser.getPhone());
        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    String shippingState = dataSnapshot.child("state").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();

                        if(shippingState.equals("delivered")){

                            txtTotalAmount.setText("Dear "+userName+ "\n Your order is on the way..");
                            recyclerView.setVisibility(View.GONE);

                            txtMsg1.setVisibility(View.VISIBLE);
                            txtMsg1.setText("Your final order has already been shipped.Within 1 hour you will receive your order at your door step, Thank You! Happy dining with mobiDine.");

                            NextProcessBtn.setVisibility(View.GONE);

                           // Toast.makeText(CartActivity.this, "", Toast.LENGTH_SHORT).show();


                        }else if (shippingState.equals("not delivered")){


                            txtTotalAmount.setText("Deliver State: Pending...");
                            recyclerView.setVisibility(View.GONE);

                            txtMsg1.setVisibility(View.VISIBLE);
                            NextProcessBtn.setVisibility(View.GONE);



                        }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });

    }


}
