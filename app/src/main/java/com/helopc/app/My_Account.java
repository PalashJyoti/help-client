package com.helopc.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;

import java.util.Objects;

public class My_Account extends AppCompatActivity {
    private Button logout,edit;
    private TextView name,phone,email,aAmount;
    private TextView apart,city,area,houseno,landmark,road,pincode;
    private ScrollView a1,a2;
    FirebaseAuth firebaseAuth;
    RelativeLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Account");
        logout = findViewById(R.id.logout);
        pincode = findViewById(R.id.pincode);
        edit = findViewById(R.id.edit);
        name = findViewById(R.id.personname);
        phone = findViewById(R.id.personphone);
        email = findViewById(R.id.personemail);
        a1 = findViewById(R.id.a1);
        area = findViewById(R.id.area);
        apart = findViewById(R.id.apartment);
        houseno = findViewById(R.id.houseno);
        city = findViewById(R.id.city);
        landmark = findViewById(R.id.landmark);
        road = findViewById(R.id.road);
        aAmount = findViewById(R.id.amount);
        layout = findViewById(R.id.layout);
        firebaseAuth = FirebaseAuth.getInstance();




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navig);
        Menu menu  = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case  R.id.nav_home:
                        finish();
                        startActivity(new Intent(My_Account.this,HomeActivity.class));
                        break;
                    case  R.id.cart:
                        finish();
                        startActivity(new Intent(My_Account.this,Cart.class));
                        break;
                    case  R.id.my_orders:
                        finish();
                        startActivity(new Intent(My_Account.this,Orders.class));
                        break;
                    case  R.id.account:
                        break;
                }

                return false;
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Personal Details");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    name.setText("Name : "+user.getName());
                    email.setText("Email : "+user.getEmail());
                    phone.setText("Phone : "+user.getPhone());

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
                   if(amount.getAmount()>0){
                       layout.setVisibility(View.VISIBLE);
                       aAmount.setText("₹"+amount.getAmount());
                   }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Address");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Address user = snapshot.getValue(Address.class);
                if (user != null) {
                    apart.setText("Apartment, Building : "+user.getApartment());
                    city.setText("City : "+user.getCity());
                    area.setText("Area : "+user.getArea());
                    houseno.setText("House no. : "+user.getHouseno());
                    landmark.setText("Landmark : "+user.getLandmark());
                    road.setText("Road : "+user.getRoad());
                    pincode.setText("Pin Code : "+user.getPincode());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),Activity_Login.class));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),EditAcountDetails.class));
            }
        });



    }
    @Override
    public void onBackPressed() {
            finish();
            startActivity(new Intent(My_Account.this,HomeActivity.class));



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

class Address{
    String apartment,area,road,city,houseno,landmark,pincode;

    public Address() {
    }

    public Address(String apartment, String area, String city, String houseno, String landmark) {
        this.apartment = apartment;
        this.area = area;
        this.city = city;
        this.houseno = houseno;
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouseno() {
        return houseno;
    }

    public void setHouseno(String houseno) {
        this.houseno = houseno;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
}

class Amount{
    int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}

class User {
    String name,phone,email;

    public User() {
    }

    public User(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}