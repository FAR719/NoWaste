package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;
    EditText mFullName, mEmail, mPassword;
    Button mRegisterButton;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Intent in = getIntent();

        // toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);

        mFullName = findViewById(R.id.fullNameEditText);
        mEmail = findViewById(R.id.emailEditText);
        mPassword = findViewById(R.id.passwordEditText);
        mRegisterButton = findViewById(R.id.registerButton);
        mLoginBtn = findViewById(R.id.rLogintextView);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.registerProgressBar);

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
            finish();
        }

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (password.length() < 6){
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // register the user in firebase
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(RegisterActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}