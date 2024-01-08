package com.helopc.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.helopc.shopheaven.app.databinding.ActivityLoginBinding;

import java.util.concurrent.TimeUnit;

public class Activity_Login extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String phoneNum;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private String verificationId;
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null){
            finish();
            startActivity(new Intent(Activity_Login.this, HomeActivity.class));
        }
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {
                SignInPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(Activity_Login.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                progressDialog.dismiss();
                Toast.makeText(Activity_Login.this,"OTP sent successfully!",Toast.LENGTH_SHORT).show();
            }
        };
        binding.getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Phone = binding.phone.getText().toString().trim();
                phoneNum = Phone;
                binding.num.setText("+91"+Phone);

                binding.l1.setVisibility(View.INVISIBLE);
                binding.l2.setVisibility(View.VISIBLE);
                binding.l1.setEnabled(false);
                binding.l2.setEnabled(true);

                if (TextUtils.isEmpty(Phone)) {
                    binding.phone.setError("Phone number is required");
                    return;
                }else {
                    SendOTP("+91"+Phone);
                }


            }
        });
        binding.resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.l2.setVisibility(View.INVISIBLE);
                binding.l1.setVisibility(View.VISIBLE);
                binding.l2.setEnabled(false);
                binding.l1.setEnabled(true);
                binding.phone.setText(phoneNum);
                binding.num.setText(phoneNum);
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = binding.otp.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    binding.otp.setError("Please enter the OTP");
                    return;
                }else {
                    Verification(verificationId,code);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        if(binding.l2.isEnabled()){
            binding.l2.setVisibility(View.INVISIBLE);
            binding.l1.setVisibility(View.VISIBLE);
            binding.l2.setEnabled(false);
            binding.l1.setEnabled(true);
        }else {
            finish();
        }


    }
    private void SendOTP(String phone){
        progressDialog.setMessage("Sending OTP");
        progressDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void Verification(String verificationId, String code) {
        progressDialog.setMessage("Verifying Code");
        progressDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        SignInPhone(credential);
    }

    private void SignInPhone(PhoneAuthCredential credential) {
        progressDialog.setMessage("Logging In");
        progressDialog.show();
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        progressDialog.dismiss();
                        finish();
                        String Phone = firebaseAuth.getCurrentUser().getPhoneNumber();
                        Intent intent = new Intent(Activity_Login.this, Add_details.class);
                        intent.putExtra("Phone", binding.phone.getText().toString().trim());
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Activity_Login.this,"Try Again ",Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                    }
                });
    }
}