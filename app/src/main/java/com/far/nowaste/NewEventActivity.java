package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.far.nowaste.objects.Evento;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

public class NewEventActivity extends AppCompatActivity {

    // definizione variabili
    Toolbar mToolbar;

    EditText mEmail, mTitle, mDesc;
    ConstraintLayout mDateBtn;
    TextView mDate;
    Button mAddBtn;

    int requestcode;
    String eventoId;
    String eventoEmail;
    String eventoTitle;
    String eventoDescription;
    int eventoYear;
    int eventoMonth;
    int eventoDay;

    int year, month, day;

    RelativeLayout layout;
    Typeface nunito;

    List<String> emails;

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

        nunito = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito);
        layout = findViewById(R.id.newEvent_layout);

        mEmail = findViewById(R.id.newEvent_editTextTextEmail);
        mTitle = findViewById(R.id.newEvent_editTextTextTitle);
        mDesc = findViewById(R.id.newEvent_editTextTextDescription);
        mDateBtn = findViewById(R.id.newEvent_dateLayout);
        mDate = findViewById(R.id.newEvent_dateTextView);
        mAddBtn = findViewById(R.id.newEvent_addBtn);

        // stabilisci se bisogna creare un nuovo evento (1) o modificarne uno esistente (2)
        Intent intent = getIntent();
        requestcode = intent.getIntExtra("com.far.nowaste.REQUESTCODE", 0);

        if (requestcode == 1) {
            // set data odierna
            CalendarDay currentDay = CalendarDay.today();
            year = currentDay.getYear();
            month = currentDay.getMonth();
            day = currentDay.getDay();
        } else if (requestcode == 2){
            mToolbar.setTitle("Modifica evento");
            eventoId = intent.getStringExtra("com.far.nowaste.EVENTO_ID");
            eventoEmail = intent.getStringExtra("com.far.nowaste.EVENTO_EMAIL");
            eventoTitle = intent.getStringExtra("com.far.nowaste.EVENTO_TITLE");
            eventoDescription = intent.getStringExtra("com.far.nowaste.EVENTO_DESCRIPTION");
            eventoYear = intent.getIntExtra("com.far.nowaste.EVENTO_YEAR", 0);
            eventoMonth = intent.getIntExtra("com.far.nowaste.EVENTO_MONTH", 0);
            eventoDay = intent.getIntExtra("com.far.nowaste.EVENTO_DAY", 0);

            year = eventoYear;
            month = eventoMonth;
            day = eventoDay;

            mEmail.setText(eventoEmail);
            mTitle.setText(eventoTitle);
            mDesc.setText(eventoDescription);
            mAddBtn.setText("Modifica");
        }

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
        builder.setTheme(R.style.ThemeOverlay_NoWaste_DatePicker);
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

                // verifica che la mail inserita corrisponda ad un account esistente
                if (email != null) {
                    boolean exist = false;
                    for (String item : emails) {
                        if (email.equals(item)) {
                            exist = true;
                            break;
                        }
                    }
                    if (!exist) {
                        mEmail.setError("L'email non corrisponde a nessun account registrato.");
                        return;
                    }
                } else {
                    showSnackbar("Errore! Riprovare.");
                }
                Evento evento = new Evento(email, title, description, year, month, day);
                if (requestcode == 1) {
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                    fStore.collection("events").add(evento).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("com.far.nowaste.NEW_EVENT_REQUEST", true);
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("LOG", "Error! " + e.getLocalizedMessage());
                            showSnackbar("Evento non creato!");
                        }
                    });
                } else if (requestcode == 2 && email.equals(eventoEmail) && title.equals(eventoTitle)
                        && description.equals(eventoDescription) && year == eventoYear
                        && month == eventoMonth && day == eventoDay){
                    finish();
                } else if (requestcode == 2) {
                    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        fStore.collection("events").document(eventoId).set(evento).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent returnIntent = new Intent();
                                returnIntent.putExtra("com.far.nowaste.MODIFY_EVENT_REQUEST", true);
                                setResult(Activity.RESULT_OK, returnIntent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {Intent returnIntent = new Intent();
                                returnIntent.putExtra("com.far.nowaste.MODIFY_EVENT_REQUEST", false);
                                setResult(Activity.RESULT_CANCELED, returnIntent);
                                finish();
                            }
                        });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // resetta emails
        emails = null;
        emails = new LinkedList<>();

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    emails.add(document.getString("email"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
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

    private void showSnackbar(String string) {
        Snackbar snackbar = Snackbar.make(layout, string, BaseTransientBottomBar.LENGTH_SHORT)
                .setBackgroundTint(ContextCompat.getColor(getApplicationContext(), R.color.snackbar))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.secondary_text));
        TextView tv = (snackbar.getView()).findViewById((R.id.snackbar_text));
        tv.setTypeface(nunito);
        snackbar.show();
    }
}