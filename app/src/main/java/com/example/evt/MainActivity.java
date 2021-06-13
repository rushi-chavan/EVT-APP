package com.example.evt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.transition.Transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.circularreveal.coordinatorlayout.CircularRevealCoordinatorLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private EditText mSignInEmail, mSignInPassword, mName, mEmail, mPassword, mRePassword, mVehicleNo;
    private Button mLogin, mSignUp;
    private TextView mLoginToggle,mSignUpToggle;

    private ConstraintLayout mSignUpLayout, mLoginLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    private Transition transition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @org.jetbrains.annotations.NotNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null) {
                    Intent intent = new Intent(MainActivity.this, MyMap.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mSignInEmail = findViewById(R.id.editText_SignIn_Email);
        mSignInPassword = findViewById(R.id.editText_SignIn_Password);
        mName = findViewById(R.id.editText_Name);
        mEmail = findViewById(R.id.editText_Email);
        mPassword = findViewById(R.id.editText_Password);
        mRePassword = findViewById(R.id.editText_Re_Password);
        mVehicleNo = findViewById(R.id.editText_Vehicle_No);

        mLogin = findViewById(R.id.btn_login);
        mSignUp = findViewById(R.id.btn_sign_up);

        mSignUpToggle = findViewById(R.id.text_sign_up);
        mLoginToggle = findViewById(R.id.text_login);

        mSignUpLayout = findViewById(R.id.layout_sign_up);
        mLoginLayout = findViewById(R.id.layout_login);
        mLoginLayout.animate().alpha(1.0f);
        mSignUpLayout.animate().alpha(0.0f);



        mSignUpToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();
                clearForms();

                mLoginLayout.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mLoginLayout.setVisibility(View.GONE);
                                mSignUpLayout.setVisibility(View.VISIBLE);
                                mSignUpLayout.animate().alpha(1.0f).setDuration(300);
                                mLoginLayout.animate().setListener(null);
                            }
                        });

            }
        });

        mLoginToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();
                clearForms();

                mSignUpLayout.animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSignUpLayout.setVisibility(View.GONE);
                                mLoginLayout.setVisibility(View.VISIBLE);
                                mLoginLayout.animate().alpha(1.0f).setDuration(300);
                                mSignUpLayout.animate().setListener(null);
                            }
                        });

            }
        });



        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();

                final String email = mSignInEmail.getText().toString();
                final String password = mSignInPassword.getText().toString();

                if(TextUtils.isEmpty(mSignInEmail.getText())) {
                    mSignInEmail.setError(" Please Enter Email ");
                }else if(TextUtils.isEmpty(mSignInPassword.getText())) {
                    mSignInPassword.setError(" Please provide a password ");
                }else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();

                final String name = mName.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String re_password = mRePassword.getText().toString();
                final String vehicle_no = mVehicleNo.getText().toString();

                if(TextUtils.isEmpty(mName.getText())) {
                    mName.setError(" Please Enter Name ");
                }else if(TextUtils.isEmpty(mEmail.getText())) {
                    mEmail.setError(" Please provide email ");
                }else if(TextUtils.isEmpty(mPassword.getText())) {
                    mPassword.setError(" Please provide a password ");
                }else if(TextUtils.isEmpty(mRePassword.getText())) {
                    mRePassword.setError(" Please provide a password ");
                }else if(TextUtils.isEmpty(mVehicleNo.getText())) {
                    mVehicleNo.setError(" Please provide a vehicle number ");
                }else if(password.equals(re_password)) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
                                current_user_db.child("name").setValue(name);
                                current_user_db.child("email_id").setValue(email);
                                current_user_db.child("vehicle_no").setValue(vehicle_no);
                            }
                        }
                    });
                }
                else {
//                    Toast.makeText(MainActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    mRePassword.setError("Password doesn't match");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthListener);
    }

    protected void clearErrors() {
        mSignInEmail.setError(null);
        mSignInPassword.setError(null);
        mName.setError(null);
        mEmail.setError(null);
        mPassword.setError(null);
        mRePassword.setError(null);
        mVehicleNo.setError(null);
    }

    protected void clearForms() {
        mSignInEmail.setText(null);
        mSignInPassword.setText(null);
        mName.setText(null);
        mEmail.setText(null);
        mPassword.setText(null);
        mRePassword.setText(null);
        mVehicleNo.setText(null);
    }
}