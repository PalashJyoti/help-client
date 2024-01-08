package com.helopc.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity implements AdapterMedCart.OnCategoryItemClickListener, PromoAdapter.ItemClickListener {
    AlertDialog.Builder builder, builder2;
    TextView amount;
    Button pay;
    String Amount, deliverycharge, couponcode, gst;
    String couponamount;
    Boolean resOpen;
    String Name;
    int q;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private ValueEventListener valueEventListener, eventListener;
    private AdapterMedCart adapterMed;
    private List<Model_Cart> medList;
    private RecyclerView recyclerView, recyclerViewcoupon;
    private PromoAdapter adapter;
    private List<Promo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerViewcoupon = findViewById(R.id.coupons);
        amount = findViewById(R.id.amount);
        pay = findViewById(R.id.payam);

        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("DeliveryCharges");
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DeliveryCharges user = snapshot.getValue(DeliveryCharges.class);
                if (user != null) {
                    deliverycharge = user.getAmount();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference dbt = FirebaseDatabase.getInstance().getReference("Tax and Other Charges");
        dbt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DeliveryCharges user = snapshot.getValue(DeliveryCharges.class);
                if (user != null) {
                    gst = user.getAmount();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Cart_Amount cart_amount1 = snapshot.getValue(Cart_Amount.class);
                if (cart_amount1 != null) {
                    amount.setText("₹ " + cart_amount1.getAmount());
                    Amount = cart_amount1.getAmount();
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Cart");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        medList = new ArrayList<>();
        adapterMed = new AdapterMedCart(getApplicationContext(), medList);
        recyclerView.setAdapter(adapterMed);
        adapterMed.setOnItemClickListener(Cart.this);
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Cart upload = postSnapshot.getValue(Model_Cart.class);
                    upload.setKey(postSnapshot.getKey());
                    medList.add(upload);

                    q += Integer.parseInt(upload.getQuantity());
                }
                adapterMed.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Promo_Codes");

        recyclerViewcoupon.setHasFixedSize(true);
        recyclerViewcoupon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        list = new ArrayList<>();
        adapter = new PromoAdapter(getApplicationContext(), list);
        recyclerViewcoupon.setAdapter(adapter);
        adapter.setOnItemClickListener(Cart.this);
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Promo upload = postSnapshot.getValue(Promo.class);
                    upload.setKey(postSnapshot.getKey());
                    list.add(upload);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Amount == null || Amount.equals("")) {
                    Toast.makeText(getApplicationContext(), "Add Items to cart first", Toast.LENGTH_SHORT).show();

                } else {
                    Intent intent = new Intent(getApplicationContext(), Payment.class);
                    intent.putExtra("Amount", Amount);
                    intent.putExtra("thing", "Product ");
                    intent.putExtra("deliverycharge", deliverycharge);
                    intent.putExtra("couponcode", couponcode);
                    intent.putExtra("couponamount", couponamount);
                    intent.putExtra("gst", gst);
                    intent.putExtra("q", q + "");


                    startActivity(intent);
                }


            }
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navig);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        finish();
                        startActivity(new Intent(Cart.this, HomeActivity.class));
                        break;
                    case R.id.cart:
                        break;
                    case R.id.my_orders:
                        finish();
                        startActivity(new Intent(Cart.this, Orders.class));
                        break;
                    case R.id.account:
                        finish();
                        startActivity(new Intent(Cart.this, My_Account.class));
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void onCategoryItemClick(int position) {

        DatabaseReference dbs = FirebaseDatabase.getInstance().getReference("Product").child(medList.get(position).getKeyid());
        dbs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Model_List_4 user = snapshot.getValue(Model_List_4.class);
                if (user != null) {
                    Name = user.getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        //  String Purpose = medList.get(position).getPurpose();
        float dmoney = Float.parseFloat(medList.get(position).getTotal());

        builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete " + Name + " from cart?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Model_Cart selectedItem = medList.get(position);
                        FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getUid()).child("Cart").child(selectedItem.getKey()).removeValue();

                        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users")
                                .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount");
                        db.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Cart_Amount cart_amount1 = snapshot.getValue(Cart_Amount.class);
                                if (cart_amount1 != null) {
                                    float money = Float.parseFloat(cart_amount1.getAmount());
                                    money = money - dmoney;
                                    amount.setText(String.valueOf(money));


                                    Cart_Amount cart_amount = new Cart_Amount();
                                    cart_amount.setAmount(String.valueOf(money));
                                    FirebaseDatabase.getInstance().getReference().child("Users")
                                            .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount")
                                            .setValue(cart_amount);
                                } else {

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Confirmation");
        alert.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Cart.this, HomeActivity.class));
    }

    @Override
    public void ItemClick(int position) {

        builder2 = new AlertDialog.Builder(this);
        builder2.setMessage("Apply  " + list.get(position).getCoupon() + " to save " + list.get(position).getPercent() + "%")
                .setPositiveButton("APPLY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        float m = Math.round(Float.parseFloat(Amount)), p = list.get(position).getPercent();
                        amount.setText("₹ " + String.valueOf(m - p * m / 100));
                        //     couponamount = String.valueOf(Integer.parseInt(medAmount)-Integer.parseInt(String.valueOf(list.get(position).getPrice())));
                        couponamount = String.valueOf(p);
                        couponcode = list.get(position).getCoupon();

                        recyclerViewcoupon.setVisibility(View.INVISIBLE);
                        recyclerViewcoupon.setEnabled(false);

                        Toast.makeText(getApplicationContext(), "Coupon Code applied", Toast.LENGTH_SHORT).show();

                    }
                });
        AlertDialog alert = builder2.create();
        alert.setTitle("Apply Promo Code");
        alert.show();


    }
}

class AdapterMedCart extends RecyclerView.Adapter<AdapterMedCart.viewHolder> {

    private Context mContext;
    private List<Model_Cart> mUploads;
    private OnCategoryItemClickListener mListener;

    public AdapterMedCart(Context context, List<Model_Cart> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Cart med = mUploads.get(position);
        holder.add.setText("Delete");
        holder.xnum.setText("x" + med.getQuantity());
        FirebaseDatabase.getInstance().getReference("Product").child(med.getKeyid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Model_List_4 user = snapshot.getValue(Model_List_4.class);
                        if (user != null) {
                            holder.name.setText(UpercaseFirstLetter(user.getName()));

                            Picasso.get()
                                    .load(user.getImage())
                                    .placeholder(R.drawable.preview_1)
                                    .into(holder.image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        holder.price.setText("₹ " + med.getAmount());


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cate_item_layout, parent, false);
        return new viewHolder(view);

    }

    public void setOnItemClickListener(OnCategoryItemClickListener listener) {
        mListener = listener;
    }

    private String UpercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }

    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name, add, xnum, price;
        ImageView image;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            add = itemView.findViewById(R.id.add);
            xnum = itemView.findViewById(R.id.xnum);
            price = itemView.findViewById(R.id.price);
            image = itemView.findViewById(R.id.image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onCategoryItemClick(position);
                }
            }
        }


    }
}


class DeliveryCharges {
    String amount;

    public DeliveryCharges() {
    }

    public DeliveryCharges(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}

class Promo {
    String coupon, key;
    int percent;

    public int getPercent() {
        return percent;
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon;
    }


    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}

class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.viewHolder> {

    private Context mContext;
    private List<Promo> mUploads;
    private ItemClickListener mListener;

    public PromoAdapter(Context context, List<Promo> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Promo uploadCurrent = mUploads.get(position);
        holder.couponcode.setText("Use Promo Code " + uploadCurrent.getCoupon());
        holder.amount.setText(uploadCurrent.getPercent() + "% OFF");


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item_layout, parent, false);
        return new viewHolder(view);

    }

    public void setOnItemClickListener(ItemClickListener listener) {
        mListener = listener;
    }

    public interface ItemClickListener {
        void ItemClick(int position);

    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView couponcode, amount;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            couponcode = itemView.findViewById(R.id.promocode);
            amount = itemView.findViewById(R.id.amount);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.ItemClick(position);
                }
            }
        }


    }

}