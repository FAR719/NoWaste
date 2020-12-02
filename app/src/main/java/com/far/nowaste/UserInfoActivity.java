package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
    }

    // onclick logout button
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(UserInfoActivity.this, "Logout effettuato.", Toast.LENGTH_SHORT).show();
        finish();
    }
}