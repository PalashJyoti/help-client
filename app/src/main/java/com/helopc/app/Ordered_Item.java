package com.helopc.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

public class Ordered_Item extends AppCompatActivity implements AdapterOrder.OnCategoryItemClickListener{

    private RecyclerView recyclerView;
    private TextView paid,amount,time,orderid,orderstatus;
    private ValueEventListener valueEventListener;
    private AdapterOrder adapterMed;
    private List<Model_Cart> medList;
    AppCompatButton cancel,generateBtn;
    String Amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordered_item);
        Bundle bundle = getIntent().getExtras();
        String key = bundle.getString("key");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Orders");

        cancel = findViewById(R.id.cancel);
        generateBtn=findViewById(R.id.generateBtn);
        orderid = findViewById(R.id.orderid);
        paid = findViewById(R.id.paid);
        amount = findViewById(R.id.amount);
        time = findViewById(R.id.time);
        orderstatus =findViewById(R.id.orderstatus);
        recyclerView = findViewById(R.id.recyclerview);

        orderid.setText("Order ID : "+key);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Orders").child(key).child("Items");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        medList = new ArrayList<>();
        adapterMed = new AdapterOrder(getApplicationContext(),medList);
        recyclerView.setAdapter(adapterMed);
        adapterMed.setOnItemClickListener(Ordered_Item.this);

        if(time.getText().equals("Item Delivered")){
            cancel.setVisibility(View.GONE);
            generateBtn.setVisibility(View.VISIBLE);
            createPDF();
        }

        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Model_Cart upload = postSnapshot.getValue(Model_Cart.class);
                    upload.setKey(postSnapshot.getKey());
                    medList.add(upload);
                }
                adapterMed.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Orders").child(key);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AmountWay user = snapshot.getValue(AmountWay.class);
                if(user!=null){
                    amount.setText("Total ₹"+user.getAmount());
                    paid.setText("Way : "+user.getWay());
                    Amount = user.getAmount();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dr =  FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Orders")
                .child(key).child("Shipping Date");

        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ShippingDate shippingDate = snapshot.getValue(ShippingDate.class);
                if(shippingDate != null){
                    time.setText("Expected delivery Time : "+shippingDate.getShippingDate());
                    orderstatus.setText("Order Confirmed Shipping Started");

                    if(shippingDate.getShippingDate().toLowerCase().equals("cancel")){
                        time.setText("Delivery Failed");
                        orderstatus.setText("Order Cancelled");
                        cancel.setVisibility(View.INVISIBLE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Delivered").child(key);
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    time.setText("Item Delivered");
                    orderstatus.setText("Order Successful");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dbc = FirebaseDatabase.getInstance().getReference("Order to be delivered").child(key);
        dbc.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    cancel.setVisibility(View.VISIBLE);
                }else {
                    cancel.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference db = FirebaseDatabase.getInstance().getReference("Order to be delivered").child(key);
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Orders").child(key);
                            databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    AmountWay user = snapshot.getValue(AmountWay.class);
                                    if(user!=null){
                                        if(user.getWay().equals("Payment Done via Online")){

                                            DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Wallet");
                                            databaseReference3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    Amount aam = snapshot.getValue(Amount.class);
                                                    Amount amount = new Amount();
                                                    if(aam!=null){
                                                        amount.setAmount(aam.getAmount()-Integer.parseInt(Amount));
                                                    }else {
                                                        amount.setAmount(Integer.parseInt(Amount));
                                                    }
                                                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getUid()).child("Wallet").setValue(amount);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });


                                        }else if(user.getWay().equals("Payment on Delivery")){
                                            FirebaseDatabase.getInstance().getReference().child("Orders").child(FirebaseAuth.getInstance().getUid()).child(key).child("status").setValue("canceled");
                                            orderstatus.setText("Canceled");
                                            orderstatus.setText("Canceled");
                                            cancel.setVisibility(View.GONE);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            DatabaseReference dbr1 = FirebaseDatabase.getInstance().getReference("Orders").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child(key);


                            DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Cancelled Orders")
                                    .child(FirebaseAuth.getInstance().getUid()).child(key);
                            ValueEventListener eventListener1 = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    db1.setValue(dataSnapshot.getValue());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            };
                            dbr1.addListenerForSingleValueEvent(eventListener1);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }
        });


    }

    private void createPDF() {
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PdfDocument myPdfDocument=new PdfDocument();
                Paint myPaint=new Paint();

                PdfDocument.PageInfo myPageInfo1=new PdfDocument.PageInfo.Builder(1200,2010,1).create();
                PdfDocument.Page myPage1=myPdfDocument.startPage(myPageInfo1);
                Canvas canvas=myPage1.getCanvas();
            }
        });

    }

    @Override
    public void onCategoryItemClick(int position) {

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
}

class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.viewHolder> {

    private Context mContext;
    private List<Model_Cart> mUploads;
    private OnCategoryItemClickListener  mListener;
    public AdapterOrder(Context context, List<Model_Cart> uploads) {
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        Model_Cart med = mUploads.get(position);
        holder.add.setText("Delete");
        holder.xnum.setText("x"+med.getQuantity());
        FirebaseDatabase.getInstance().getReference("Product").child(med.getKeyid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Model_List_4 user = snapshot.getValue(Model_List_4.class);
                        if(user!=null ){
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

        holder.price.setText("₹ "+med.getAmount());
        holder.add.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_cate_item_layout,parent,false);
        return new viewHolder(view);

    }



    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name,add,xnum,size,price;
        ImageView image;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            add = itemView.findViewById(R.id.add);
            xnum = itemView.findViewById(R.id.xnum);
            size = itemView.findViewById(R.id.size);
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
    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);

    }
    public void setOnItemClickListener(OnCategoryItemClickListener  listener) {
        mListener = listener;
    }
    private String UpercaseFirstLetter(String newText) {
        String firstLetter = newText.substring(0, 1);
        String remainingLetters = newText.substring(1, newText.length());
        firstLetter = firstLetter.toUpperCase();
        newText = firstLetter + remainingLetters;
        return newText;
    }
}

class PrescriptionStatus{
    String prescriptionStatus;

    public PrescriptionStatus(String prescriptionStatus) {
        this.prescriptionStatus = prescriptionStatus;
    }

    public PrescriptionStatus() {
    }

    public String getPrescriptionStatus() {
        return prescriptionStatus;
    }

    public void setPrescriptionStatus(String prescriptionStatus) {
        this.prescriptionStatus = prescriptionStatus;
    }
}

class ShippingDate{
    String shippingDate,orderConfirmTime;

    public ShippingDate() {
    }

    public ShippingDate(String shippingDate,String orderConfirmTime) {
        this.shippingDate = shippingDate;
        this.orderConfirmTime = orderConfirmTime;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getOrderConfirmTime() {
        return orderConfirmTime;
    }

    public void setOrderConfirmTime(String orderConfirmTime) {
        this.orderConfirmTime = orderConfirmTime;
    }
}

class Med{
    String Name,Price,key,Xnum,Category,PImgUrl,type,size;
    public Med() {
    }

    public Med(String name, String price, String xnum, String category, String PImgUrl, String type) {
        Name = name;
        Price = price;
        Xnum = xnum;
        Category = category;
        this.PImgUrl = PImgUrl;
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getXnum() {
        return Xnum;
    }

    public void setXnum(String xnum) {
        Xnum = xnum;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPImgUrl() {
        return PImgUrl;
    }

    public void setPImgUrl(String PImgUrl) {
        this.PImgUrl = PImgUrl;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
}

class Cart_Amount{
    String amount,key;

    public Cart_Amount() {
    }

    public Cart_Amount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getKey() {
        return key;
    }
    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}

class Sizes{
    String size;
    int amount;
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}