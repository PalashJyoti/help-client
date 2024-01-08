package com.helopc.app;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.RED;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Product extends AppCompatActivity implements Adapter_Product_Images.OnCategoryItemClickListener,Adapter_Des.OnCategoryItemClickListener,Adapter_Recom.OnCategoryItemClickListener{

    RecyclerView recyclerview2,recyclerView;
    private Adapter_Product_Images adapter_list_2;
    private List<Model_Product_Images> list_2;
    private ValueEventListener valueEventListener_2;
    String keyid,Amount;

    AppCompatButton plus,minus,add;
    TextView value,product,amount,offer,recomm;
    boolean S;


    RecyclerView recyclerviewdes;
    private Adapter_Des adapter_des;
    private List<Model_Description> list_des;
    private ValueEventListener valueEventListener_3;
    Spinner spinner;
    String clicked=null;
    int Value=1;


    private ValueEventListener mDBListener;
    Adapter_Recom adapter;
    private ArrayList<Model_List_4> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);



        recyclerview2 = findViewById(R.id.recyclerview);
        recyclerviewdes = findViewById(R.id.recyclerviewdes);
        spinner = findViewById(R.id.spinner);
        recomm = findViewById(R.id.recomm);
        recyclerView = findViewById(R.id.recyclerView);
        value = findViewById(R.id.value);
        plus = findViewById(R.id.plus);
        minus = findViewById(R.id.minus);
        add = findViewById(R.id.add);
        product = findViewById(R.id.product);
        amount = findViewById(R.id.amount);
        offer=findViewById(R.id.offer);
        Bundle bundle = getIntent().getExtras();
        keyid = bundle.getString("keyid");

        DatabaseReference dbs = FirebaseDatabase.getInstance().getReference("Product").child(keyid);

        dbs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Model_List_4 user = snapshot.getValue(Model_List_4.class);
                if(user!=null){
                    product.setText(UppercaseFirstLetter(user.getName()));

                    if(user.getOffer()!=null && !(user.getOffer().equals("0.00"))){
                        amount.setText("Rs. "+user.getOfferAmount());
                        Amount=user.getOfferAmount();
                        offer.setText("("+user.getOffer()+" % off)");
                        offer.setVisibility(View.VISIBLE);
                    }else {
                        amount.setText("Rs. "+user.getAmount());
                        Amount=user.getAmount();
                        offer.setVisibility(View.GONE);
                    }

                    DatabaseReference dbre = FirebaseDatabase.getInstance().getReference("Product Category Wise").child(user.getCategory());

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
                    linearLayoutManager.setStackFromEnd(true);
                    linearLayoutManager.setReverseLayout(true);
                    recyclerView.setHasFixedSize(true);
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    list = new ArrayList<>();
                    adapter = new Adapter_Recom(getApplicationContext(),list);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnItemClickListener(Product.this);
                    valueEventListener_3 = dbre.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                recomm.setVisibility(View.VISIBLE);
                                list.remove(keyid);
                                Model_List_4 upload = postSnapshot.getValue(Model_List_4.class);
                         //       upload.setKey(postSnapshot.getKey());
                                if(!keyid.equals(upload.getKeyid())){
                                    list.add(upload);
                                }




                            }
                            adapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Product Images").child(keyid);

        recyclerview2.setHasFixedSize(true);
        recyclerview2.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
        list_2 = new ArrayList<>();
        adapter_list_2 = new Adapter_Product_Images(getApplicationContext(),list_2);
        recyclerview2.setAdapter(adapter_list_2);
        adapter_list_2.setOnItemClickListener(Product.this);
        valueEventListener_2 = databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_2.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    recyclerview2.setVisibility(View.VISIBLE);
                    Model_Product_Images upload = postSnapshot.getValue(Model_Product_Images.class);
                    upload.setKey(postSnapshot.getKey());
                    list_2.add(upload);

                }
                adapter_list_2.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Product Description").child(keyid);

        recyclerviewdes.setHasFixedSize(true);
        recyclerviewdes.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        list_des = new ArrayList<>();
        adapter_des = new Adapter_Des(getApplicationContext(),list_des);
        recyclerviewdes.setAdapter(adapter_des);
        adapter_des.setOnItemClickListener(Product.this);
        valueEventListener_3 = databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_des.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    recyclerviewdes.setVisibility(View.VISIBLE);
                    Model_Description upload = postSnapshot.getValue(Model_Description.class);
                    upload.setKey(postSnapshot.getKey());
                    list_des.add(upload);

                }
                adapter_des.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        DatabaseReference dbCATECity = FirebaseDatabase.getInstance().getReference().child("Product Colour").child(keyid);
        dbCATECity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> areas = new ArrayList<String>();

                for (DataSnapshot areaSnapshot: dataSnapshot.getChildren()) {
                    String areaName = areaSnapshot.child("colour").getValue(String.class);
                    areas.add(UppercaseFirstLetter(areaName));
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Product.this, R.layout.style_spinner, areas);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(areasAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        if(spinner != null && spinner.getSelectedItem() !=null ) {
                            FirebaseDatabase.getInstance().getReference("Product Colour").child(keyid).child(spinner.getSelectedItem().toString().toLowerCase())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ColourWise colourWise = snapshot.getValue(ColourWise.class);

                                            if(!(offer.getVisibility()==View.VISIBLE)){
                                                amount.setText("Rs. "+colourWise.getAmount());
                                                Amount = colourWise.getAmount();
                                            }else {
                                                amount.setText("Rs. "+Amount);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                            DatabaseReference dbS = FirebaseDatabase.getInstance().getReference("Product Colour and Stock").child(keyid).child(spinner.getSelectedItem().toString().toLowerCase());
                            dbS.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Model_Size user = snapshot.getValue(Model_Size.class);
                                    if(user!=null){
                                        if(user.isStock()==false){

                                            amount.setText("Out of Stock");
                                            offer.setVisibility(View.GONE);
                                            amount.setTextColor(RED);
                                            add.setEnabled(false);
                                            add.setBackgroundColor(GRAY);
                                        }else {
                                            add.setEnabled(true);
                                            add.setBackgroundColor(Color.parseColor("#0D2067"));
                                            amount.setTextColor(BLACK);
                                            offer.setVisibility(View.VISIBLE);
                                        }
                                    }else {
                                        S = false;
                                        add.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });


        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Value>1){
                    Value = Value-1;
                    value.setText(""+Value);
                }
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Value = Value+1;
                value.setText(""+Value);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                    String currentDate = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
                    String currentTime = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
                    String currentMili = currentDate+currentTime;
                    Float total = Value*Float.parseFloat(Amount);

                    Model_Cart order = new Model_Cart();
                    order.setAmount(Amount);
                    order.setColour(spinner.getSelectedItem().toString());
                    order.setKey(currentMili);
                    order.setKeyid(keyid);
                    order.setQuantity(String.valueOf(Value));
                    order.setTotal(String.valueOf(total));
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Cart").child(currentMili).setValue(order)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getApplicationContext(),"Item added to Cart",Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount");
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Cart_Amount cart_amount1 = snapshot.getValue(Cart_Amount.class);
                            if(cart_amount1 != null){
                                Float money = Float.parseFloat(cart_amount1.getAmount());
                                money = money + total;
                                Cart_Amount cart_amount = new Cart_Amount();
                                cart_amount.setAmount(String.valueOf(money));
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount")
                                        .setValue(cart_amount);
                            }
                            else {
                                Cart_Amount cart_amount = new Cart_Amount();
                                cart_amount.setAmount(String.valueOf(total));
                                FirebaseDatabase.getInstance().getReference().child("Users")
                                        .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount")
                                        .setValue(cart_amount);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }
        });

    }

    @Override
    public void onCategoryItemClick2(int position) {

    }

    @Override
    public void onCategoryItemClick4(int position) {
        finish();
        Intent intent = new Intent(getApplicationContext(),Product.class);
        intent.putExtra("keyid",list.get(position).getKeyid());
        startActivity(intent);
    }
    private String UppercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }

    @Override
    public void onCategoryItemClickDEs(int position) {

    }
}

class Model_Size{
    boolean stock;

    public boolean isStock() {
        return stock;
    }
}

class ColourWise{
    String colour,amount;

    public String getColour() {
        return colour;
    }

    public String getAmount() {
        return amount;
    }
}

class Model_Cart{
    String keyid,key,amount,quantity,colour,total;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }
}

class Adapter_Recom extends RecyclerView.Adapter<Adapter_Recom.viewHolder> {

    private Context mContext;
    private List<Model_List_4> mUploads;
    private OnCategoryItemClickListener mListener;
    public Adapter_Recom(Context context, List<Model_List_4> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_List_4 med = mUploads.get(position);

        Picasso.get()
                .load(med.getImage())
                .into(holder.image);
        holder.name.setText(med.getName());
        holder.category.setText(med.getCategory());
        holder.category.setVisibility(View.GONE);
        holder.price.setText("₹"+med.getAmount());

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recom,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView image;
        TextView name,category,price;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
            price = itemView.findViewById(R.id.price);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick4(position);
                }
            }
        }


    }
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick4(int position);

    }
    public void setOnItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }
}