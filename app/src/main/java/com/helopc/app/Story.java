package com.helopc.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.helopc.shopheaven.app.R;
import com.squareup.picasso.Picasso;

public class Story extends AppCompatActivity {

    ImageView image;
    AppCompatButton buttonC, buttonP;
    LinearLayout Link;
    String type, key, category, product, link, storyImage, keyid,Image;
    TextView pro;

    private int CurrentProgress = 0;
    private ProgressBar progressBar;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        image = findViewById(R.id.image);
        buttonC = findViewById(R.id.buttonC);
        buttonP = findViewById(R.id.buttonP);
        Link = findViewById(R.id.link);
        pro = findViewById(R.id.pro);
        progressBar = findViewById(R.id.progressBar);

        Bundle bundle = getIntent().getExtras();
        type = bundle.getString("type");
        key = bundle.getString("key");
        category = bundle.getString("category");
        product = bundle.getString("product");
        link = bundle.getString("link");
        storyImage = bundle.getString("storyImage");
        keyid = bundle.getString("keyid");
        Image = bundle.getString("image");

        if (storyImage != null) {
            Picasso.get()
                    .load(storyImage)
                    .into(image);
        }

        Link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.chrome");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    intent.setPackage(null);
                    startActivity(intent);
                }
            }
        });

        if (type.toLowerCase().equals("link")) {
            Link.setVisibility(View.VISIBLE);
        }else
        if (type.equalsIgnoreCase("product")) {
            buttonP.setVisibility(View.VISIBLE);
            buttonP.setText("Buy Now");
            pro.setText(product);

        }else
        if (type.equalsIgnoreCase("category")) {
            buttonC.setVisibility(View.VISIBLE);
            buttonC.setText("Buy Now");
            pro.setText(category);

        }else {
            Toast.makeText(getApplicationContext(),"Nothing beta",Toast.LENGTH_SHORT).show();
        }

        buttonP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Product.class);
                intent.putExtra("keyid",keyid);
                startActivity(intent);

            }
        });
        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Product_List_Under_Category.class);
                intent.putExtra("category",category);
                startActivity(intent);

            }
        });

    /*    startTimer();

        CountDownTimer mCountDownTimer;

        progressBar.setProgress(i);
        mCountDownTimer=new CountDownTimer(5000,1000) {

            @Override
            public void onTick(long millisUntilFinished) {
        //        Log.v("Log_tag", "Tick of Progress"+ i+ millisUntilFinished);
                i++;
                progressBar.setProgress((int)i*100/(5000/1000));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                progressBar.setProgress(100);
            }
        };
        mCountDownTimer.start();*/

    }



/*    public void startTimer() {


        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                finish();
            }

        }.start();

    }*/


}