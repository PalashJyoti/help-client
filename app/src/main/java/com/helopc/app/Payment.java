package com.helopc.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.helopc.shopheaven.app.R;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Payment extends AppCompatActivity  implements PaymentResultListener {
    private TextView thing,amount,Deliverycharge,Payable,Couponamount,Gst;
    private TextView pay,payond,coupontext,wallet;
    private String medAmount , thething,deliverycharge,couponcode,couponamount,finalAmount;
    private String name,email,phone,apartment,city,area,houseno,landmark,gst;
    String Name,Email,Phone;
    float aAmount;

    float g;
    int q;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);
        thing = findViewById(R.id.thing);
        amount = findViewById(R.id.amount);
        pay = findViewById(R.id.pay);
        payond = findViewById(R.id.payond);
        Gst = findViewById(R.id.gst);
        coupontext = findViewById(R.id.coupontext);

        Deliverycharge = findViewById(R.id.decharge);
        Couponamount = findViewById(R.id.amountdeduct);
        Payable = findViewById(R.id.payable);
        wallet = findViewById(R.id.wallet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     /*   ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cart");
*/
        Bundle bundle = getIntent().getExtras();
        medAmount = bundle.getString("Amount");
        thething = bundle.getString("thing");
        deliverycharge = bundle.getString("deliverycharge");
        couponcode = bundle.getString("couponcode");
        couponamount = bundle.getString("couponamount");
        gst = bundle.getString("gst");
        q = Integer.parseInt(bundle.getString("q"));



        //  thing.setText("Full Amount");
        amount.setText("₹"+medAmount);

        if(gst!=null){
            g = Integer.valueOf(gst);
        }
        if(deliverycharge!=null || !deliverycharge.equals("0")){
            deliverycharge = String.valueOf(q*Integer.parseInt(deliverycharge));
            Deliverycharge.setText(" ₹"+deliverycharge);
        }


        Gst.setText("₹"+g*Float.parseFloat(medAmount)/100);


        if(couponamount==null|| couponamount.equals("")){
            float m = Float.parseFloat(medAmount),d=Float.parseFloat(deliverycharge);
            Couponamount.setVisibility(View.INVISIBLE);
            coupontext.setVisibility(View.INVISIBLE);
            Payable.setText(" ₹"+String.valueOf(m+d+g*m/100));


            finalAmount = String.valueOf(m+d+g*m/100);
        }else {
            float m = Float.parseFloat(medAmount),p=Float.parseFloat(couponamount),d=Float.parseFloat(deliverycharge);

            Couponamount.setText(" - "+couponamount+"%");
            Payable.setText(" ₹"+String.valueOf(m+d-p*m/100+g*m/100));


            finalAmount = String.valueOf(m+d-p*m/100+g*m/100);
        }




        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartPayment(thething,finalAmount);
            }
        });
        payond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceOrder("Payment on Delivery");
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Personal Details");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    Name = user.getName();
                    Email = user.getEmail();
                    Phone = user.getPhone();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference dba = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Wallet");
        dba.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Amount amount = snapshot.getValue(Amount.class);
                if (amount != null) {
                    if(amount.getAmount()>=Integer.parseInt(finalAmount)){
                        wallet.setVisibility(View.VISIBLE);
                        aAmount = amount.getAmount();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaceOrder("Payment Done via Online");
                Amount amount = new Amount();
                amount.setAmount(Math.round(aAmount-Float.parseFloat(finalAmount)));
                FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Wallet").setValue(amount);

            }
        });
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
    private void PlaceOrder(String paymentMode) {

        String cd = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        String ct = new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
        final String id = cd+ct;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Cart");

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(FirebaseAuth.getInstance().getUid()).child(id).child("Items");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                db.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            //        Toast.makeText(getApplicationContext(), "Order Placed Successfully......", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed......", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);


        DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Orders").child(id).child("Items");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dbr.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            //          Toast.makeText(getApplicationContext(), "Success......", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed......", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        databaseReference.addListenerForSingleValueEvent(eventListener);



        DatabaseReference dbr1 = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Personal Details");

        DatabaseReference dbr2 = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Address");

        DatabaseReference db1 = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(FirebaseAuth.getInstance().getUid()).child(id).child("Personal Details");
        DatabaseReference db2 = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(FirebaseAuth.getInstance().getUid()).child(id).child("Address");
        ValueEventListener eventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                db1.setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        dbr1.addListenerForSingleValueEvent(eventListener1);

        ValueEventListener eventListener2 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                db2.setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        dbr2.addListenerForSingleValueEvent(eventListener2);
        //  Date currentTime = Calendar.getInstance().getTime();

        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy / MM / dd at HH : mm : ss", Locale.getDefault());
        // String currentDateandTime = sdf.format(new Date());

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        AmountWay amountWay = new AmountWay();
        amountWay.setAmount(finalAmount);
        amountWay.setWay(paymentMode);
        amountWay.setDate(currentDate+" at "+currentTime);

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid()).child("Orders").child(id).setValue(amountWay);

        FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(FirebaseAuth.getInstance().getUid()).child(id).setValue(amountWay).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Your order has been placed \n Thank you for Purchasing from us. ", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid()).child("Cart").removeValue();
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid()).child("Cart_Amount").removeValue();
            }
        });
        DatabaseReference dbn = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Personal Details");
        dbn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {

                    DatabaseReference dbaa = FirebaseDatabase.getInstance().getReference("Admin Data");
                    dbaa.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Admin_Data user2 = snapshot.getValue(Admin_Data.class);
                            if (user2 != null) {

                                SendBulkSms(user.getName(),medAmount,id,user2.getNumber());
                            }
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


    }

    private void SendBulkSms(String UserName,String BookingPrice,String BookingID,String MerchantNumber){
        String newApi = "AIYgFlyqaStKTZxUrNspiJWMn6G052X487LkoEOvdDjz3mQ1CBo5QIC84rX7PsuxDTFl6aRBkLAWNUwJ";
        String newUrl = "https://www.fast2sms.com/dev/bulkV2?authorization="+newApi+"&route=dlt&sender_id=VINTRO&message=135013&variables_values="+UserName+"|"+BookingPrice+"|"+BookingID+"&flash=0&numbers="+MerchantNumber;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(newUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
            }
        });
        finish();
        startActivity(new Intent(getApplicationContext(), Payment_Successful.class));
    }
    public void StartPayment(String filename,String amt) {
        int amount = Math.round(Float.parseFloat(amt) * 100);

        Checkout checkout = new Checkout();
        /**
         * Set your logo here
         */
        // checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: Rentomojo || HasGeek etc.
             */
            options.put("name", "Vintro");

            options.put("description", "Purpose : "+filename);

            options.put("currency", "INR");
            options.put("data-prefill.name", "My name");
            /**
             * Amount is always passed in PAISE
             * Eg: "500" = Rs 5.00
             */
            JSONObject preFill = new JSONObject();
            preFill.put("email", Email);
            preFill.put("contact", Phone);
            options.put("prefill", preFill);

            options.put("amount", amount);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }
    @Override
    public void onPaymentSuccess(String s) {
        PlaceOrder("Payment Done via Online");
    }


    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getApplicationContext(), "Payment not successful", Toast.LENGTH_SHORT).show();

    }
}

class AmountWay{
    String amount,way,key,date;

    public AmountWay() {
    }

    public AmountWay(String amount, String way, String key,String date) {
        this.amount = amount;
        this.way = way;
        this.key = key;
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

class Admin_Data{
    String name,number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}