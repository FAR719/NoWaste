package com.far.nowaste;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class RegisterActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
    }
}