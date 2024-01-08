package com.helopc.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.helopc.shopheaven.app.R;
import com.helopc.shopheaven.app.databinding.ActivityCustomPcBinding;

public class CustomPcActivity extends AppCompatActivity {
    ActivityCustomPcBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCustomPcBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String number="+918638040375";
        String text="Hello";

        binding.whatsappBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean installed= isAppInstalled("com.whatsapp");

                if (installed){
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+number+"&text="+text));
                    startActivity(intent);
                }else{
                    Toast.makeText(CustomPcActivity.this, "WhatsApp is not installed. Please install the app first.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean isAppInstalled(String s) {
        PackageManager packageManager=getPackageManager();
        boolean is_installed;

        try {
            packageManager.getPackageInfo(s,PackageManager.GET_ACTIVITIES);
            is_installed=true;
        } catch (PackageManager.NameNotFoundException e) {
            is_installed=false;
            e.printStackTrace();
        }
        return is_installed;
    }
}