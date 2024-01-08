package com.helopc.app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.helopc.shopheaven.app.R;

public class Refund_Policy extends AppCompatActivity {
   // private int phone_permission = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund__policy);
        Bundle bundle = getIntent().getExtras();
        String title = bundle.getString("title");
        String thing = bundle.getString("thing");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);

        TextView Thing = findViewById(R.id.thing);
        Thing.setText(thing);

   /*     Button mail = findViewById(R.id.mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mailid = "medsaathi@gmail.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{ mailid});

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        Button contact = findViewById(R.id.contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
                    String  phone =   "9707861242";

                    if(phone.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Phone number not available",Toast.LENGTH_SHORT).show();
                    }else {
                        String tel = "tel: "+ phone;
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(tel));
                        startActivity(intent);

                    }   }else {
                    RequestPermission();
                }

            }

        }); */
    }/*
    private void RequestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(Refund_Policy.this,Manifest.permission.CALL_PHONE)){
            new AlertDialog.Builder(getApplicationContext())
                    .setTitle("Permission needed")
                    .setMessage("Permission needed to make call")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Refund_Policy.this,new String[] {Manifest.permission.CALL_PHONE},phone_permission);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }else {
            ActivityCompat.requestPermissions(Refund_Policy.this,new String[] {Manifest.permission.CALL_PHONE},phone_permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == phone_permission){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                String  phone =   "9707861242";

                if(phone.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Phone number not available",Toast.LENGTH_SHORT).show();
                }else {
                    String tel = "tel: "+ phone;
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(tel));
                    startActivity(intent);

                }
            }else {
                Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    } */
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