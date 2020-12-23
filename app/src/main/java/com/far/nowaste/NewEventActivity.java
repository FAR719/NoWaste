package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.far.nowaste.objects.Evento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NewEventActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    EditText mEmail, mTitle, mDesc;
    ConstraintLayout mDateBtn;
    TextView mDate;
    Button mAddBtn;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        // toolbar
        mToolbar = findViewById(R.id.newEvent_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmail = findViewById(R.id.newEvent_editTextTextEmail);
        mTitle = findViewById(R.id.newEvent_editTextTextTitle);
        mDesc = findViewById(R.id.newEvent_editTextTextDescription);
        mDateBtn = findViewById(R.id.newEvent_dateLayout);
        mDate = findViewById(R.id.newEvent_dateTextView);
        mAddBtn = findViewById(R.id.newEvent_addBtn);

        // set data odierna
        CalendarDay currentDay = CalendarDay.today();
        year = currentDay.getYear();
        month = currentDay.getMonth();
        day = currentDay.getDay();

        String dayString, monthString;
        if (day < 10) {
            dayString = "0" + day;
        } else {
            dayString = day + "";
        }
        if (month < 10) {
            monthString = "0" + month;
        } else {
            monthString = month + "";
        }
        mDate.setText(dayString + "/" + monthString + "/" + year);

        // date picker
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        final long today = MaterialDatePicker.todayInUtcMilliseconds();
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Seleziona una data");
        builder.setSelection(today);
        builder.setTheme(R.style.CustomMaterialDatePicker);
        final MaterialDatePicker materialDatePicker = builder.build();

        mDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(materialDatePicker);
            }
        });
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String title = mTitle.getText().toString().trim();
                String description = mDesc.getText().toString().trim();

                // controlla le info aggiunte

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Inserisci l'email del destinatario.");
                    return;
                }

                if (TextUtils.isEmpty(title)) {
                    mTitle.setError("Inserisci il titolo dell'evento.");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    mDesc.setError("Inserisci una descrizione dell'evento.");
                    return;
                }

                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                Evento evento = new Evento(email, title, description, year, month, day);
                fStore.collection("events").add(evento).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(NewEventActivity.this, "Evento creato.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDatePicker(MaterialDatePicker materialDatePicker) {
        materialDatePicker.show(getSupportFragmentManager(), "DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                // get selected date
                Date date = new Date(Long.parseLong(selection.toString()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH) + 1;
                day = calendar.get(Calendar.DAY_OF_MONTH);
                String dayString, monthString;
                if (day < 10) {
                    dayString = "0" + day;
                } else {
                    dayString = day + "";
                }
                if (month < 10) {
                    monthString = "0" + month;
                } else {
                    monthString = month + "";
                }
                mDate.setText(dayString + "/" + monthString + "/" + year);
            }
        });
    }
}