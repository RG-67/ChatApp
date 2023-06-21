package com.project.chatapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.project.chatapp.Utils.Session;
import com.project.chatapp.databinding.ActivityVerificationBinding;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity {

    ActivityVerificationBinding binding;
    private FirebaseAuth mAuth;
    private String userID = "";
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = new Session(VerificationActivity.this);
        checkSession();

        mAuth = FirebaseAuth.getInstance();
        getOTP();
        verifyOTP();

    }

    private void checkSession() {
        if (!session.getUserID().equals("")) {
            startActivity(new Intent(VerificationActivity.this, MainActivity.class));
            finish();
        }
    }

    private void getOTP() {
        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.rel1.setVisibility(View.GONE);
                binding.rel3.setVisibility(View.VISIBLE);
                binding.textNumber.setText("Code is sent to " + binding.number.getText().toString());
                String number = "+91" + binding.number.getText().toString();
                setTimer();
                sendVerificationCode(number);
            }
        });
    }

    private void setTimer() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisInFuture) {
                binding.text2.setText(millisInFuture/1000 + "");
            }

            @Override
            public void onFinish() {
                binding.rel2.setVisibility(View.VISIBLE);
                binding.rel4.setVisibility(View.GONE);
            }
        }.start();
    }

    private void verifyOTP() {
        binding.verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyCode(binding.otp.getText().toString());
            }
        });
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(number)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(mCallBack)
                .setActivity(this)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            userID = s;
            session.setUserID(userID);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            binding.otp.setText(code);
            if (code != null) {
                binding.otp.setText(code);
                verifyCode(code);
            }
            else {
                Toast.makeText(VerificationActivity.this, "Verification ID is null", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerificationActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(userID, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    Toast.makeText(VerificationActivity.this, "Task is successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(VerificationActivity.this, MainActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(VerificationActivity.this, "Task is not successfull", Toast.LENGTH_SHORT).show();
                    requestAgain();
                }
            }
        });
    }

    private void requestAgain() {
        binding.requestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCode("+91" + binding.number.getText().toString());
            }
        });
    }

}