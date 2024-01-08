package com.helopc.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.helopc.shopheaven.app.R;

import java.util.Objects;

public class EditAcountDetails extends AppCompatActivity {


    private TextInputEditText ename,ephone,eemail;
    private TextInputEditText eapart,earea,ehouseno,elandmark,road,pincode,city;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_acount_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Edit Account Details");
        save = findViewById(R.id.save);
        ename = findViewById(R.id.editpersonname);
        ephone = findViewById(R.id.editpersonphone);
        eemail = findViewById(R.id.editpersonemail);
        earea = findViewById(R.id.editarea);
        eapart = findViewById(R.id.editapartment);
        ehouseno = findViewById(R.id.edithouseno);
        city = findViewById(R.id.city);
        elandmark = findViewById(R.id.editlandmark);
        road = findViewById(R.id.road);
        pincode = findViewById(R.id.pincode);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).child("Personal Details");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    ename.setText(user.getName());
                    eemail.setText(user.getEmail());
                    ephone.setText(user.getPhone());
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
                    eapart.setText(user.getApartment());
                    earea.setText(user.getArea());
                    ehouseno.setText(user.getHouseno());
                    elandmark.setText(user.getLandmark());
                    road.setText(user.getRoad());
                    pincode.setText(user.getPincode());
                    city.setText(user.getCity());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = ename.getText().toString().trim();
                String Phone = ephone.getText().toString().trim();
                String Email = eemail.getText().toString().trim();

                User user = new User();
                user.setEmail(Email);
                user.setPhone(Phone);
                user.setName(Name);
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Personal Details").setValue(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getApplicationContext(),"Details updated ",Toast.LENGTH_SHORT).show();

                            }
                        });
                String Apartment = eapart.getText().toString().trim();
                String Houseno = ehouseno.getText().toString().trim();
                String Area = earea.getText().toString().trim();
                String Landmark = elandmark.getText().toString().trim();

                Address address = new Address();

                address.setApartment(Apartment);
                address.setArea(Area);
                address.setCity(city.getText().toString());
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