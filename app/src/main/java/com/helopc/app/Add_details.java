package com.helopc.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;

import java.util.Objects;

public class Add_details extends AppCompatActivity {

    TextInputEditText name,phone;
    Button add;
    FirebaseAuth firebaseAuth;
    TextInputEditText editarea,editapartment,edithouseno,editlandmark,road,pincode,city;
    String Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        add = findViewById(R.id.add);
        editarea = findViewById(R.id.editarea);
        editapartment = findViewById(R.id.editapartment);
        edithouseno = findViewById(R.id.edithouseno);
        editlandmark = findViewById(R.id.editlandmark);
        road = findViewById(R.id.road);
        pincode = findViewById(R.id.pincode);
        city = findViewById(R.id.city);

        firebaseAuth = FirebaseAuth.getInstance();
        Bundle bundle = getIntent().getExtras();
        Phone = bundle.getString("Phone");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Personal Details");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot:snapshot.getChildren()) {
                    if (postSnapshot.exists()){
                        finish();
                        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = name.getText().toString().trim();
                String Email = phone.getText().toString().trim();

                User user = new User();
                user.setEmail(Email);
                user.setPhone(Phone);
                user.setName(Name);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Personal Details").setValue(user);
                String Apartment = editapartment.getText().toString().trim();
                String City = city.getText().toString().toLowerCase();
                String Houseno = edithouseno.getText().toString().trim();
                String Area = editarea.getText().toString().trim();
                String Landmark = editlandmark.getText().toString().trim();

                Address address=new Address();

                address.setApartment(Apartment);
                address.setArea(Area);
                address.setCity(City);
                address.setHouseno(Houseno);
                address.setLandmark(Landmark);
                address.setPincode(pincode.getText().toString().trim());
                address.setRoad(road.getText().toString().trim());

                FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Address").setValue(address);

                finish();
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            }
        });
    }
}