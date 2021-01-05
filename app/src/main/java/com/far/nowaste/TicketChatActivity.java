package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.far.nowaste.objects.Message;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TicketChatActivity extends AppCompatActivity {
    // definizione variabili
    Toolbar mToolbar;

    EditText mRisposta;
    ImageButton mRispBtn;

    RecyclerView mFirestoreList;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_chat);

        // toolbar
        mToolbar = findViewById(R.id.ticketChat_toolbar);
        setSupportActionBar(mToolbar);

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String identificativo = intent.getStringExtra("com.far.nowaste.identificativo");
        String oggetto = intent.getStringExtra("com.far.nowaste.oggetto");

        mToolbar.setTitle(oggetto);

        fAuth = FirebaseAuth.getInstance();

        mFirestoreList = findViewById(R.id.ticketChat_recyclerView);
        mRisposta = findViewById(R.id.rRispostaEditText);

        if(fAuth.getCurrentUser() == null){
            finish();
        }

        // query
        FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        Query query = fstore.collection("tickets").document(identificativo).collection("messages")
                .orderBy("year").orderBy("month").orderBy("day").orderBy("hour").orderBy("minute").orderBy("second");

        // recyclerOptions
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        adapter = new FirestoreRecyclerAdapter<Message, TicketChatActivity.ChatViewHolder>(options) {
            @NonNull
            @Override
            public TicketChatActivity.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_chat_message_item, parent, false);
                return new TicketChatActivity.ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TicketChatActivity.ChatViewHolder holder, int position, @NonNull Message model) {
                String day, month, hour, minute;
                if (model.getDay() < 10) {
                    day = "0" + model.getDay();
                } else {
                    day = model.getDay() + "";
                }
                if (model.getMonth() < 10) {
                    month = "0" + model.getMonth();
                } else  {
                    month = model.getMonth() + "";
                }
                if (model.getHour() < 10) {
                    hour = "0" + model.getHour();
                } else {
                    hour = model.getHour() + "";
                }
                if (model.getMinute() < 10) {
                    minute = "0" + model.getMinute();
                } else  {
                    minute = model.getMinute() + "";
                }

                if (model.isOperatore()){
                    holder.operatoreDate.setText(day + "/" + month + "/" + model.getYear());
                    holder.operatoreLayout.setVisibility(View.VISIBLE);
                    holder.userLayout.setVisibility(View.GONE);
                    holder.operatoreMessage.setText(model.getTesto());
                    holder.operatoreHour.setText(hour + ":" +  minute);
                } else {
                    holder.userDate.setText(day + "/" + month + "/" + model.getYear());
                    holder.userLayout.setVisibility(View.VISIBLE);
                    holder.operatoreLayout.setVisibility(View.GONE);
                    holder.userMessage.setText(model.getTesto());
                    holder.userHour.setText(hour + ":" + minute);
                }
            }
        };


        // View Holder
        mFirestoreList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mFirestoreList.setLayoutManager(linearLayoutManager);
        mFirestoreList.setAdapter(adapter);

        //  risposta
        mRispBtn = findViewById(R.id.rSentimageButton);
        mRispBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String risposta = mRisposta.getText().toString();
                // controlla la info aggiunte
                if (TextUtils.isEmpty(risposta)){
                    mRisposta.setError("Inserisci risposta.");
                    return;
                }

                saveAnswer(risposta,identificativo);
                mRisposta.setText("");

                // Setta lo Stato del Ticket = aperto all'invio del messaggio
                Map<String, Object> statoTickets = new HashMap<>();
                statoTickets.put("stato",true);
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                fStore.collection("tickets").document(identificativo).update(statoTickets).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("LOG", "Error! " + e.getLocalizedMessage());
                    }
                });
            }
        });
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder{

        ConstraintLayout userLayout;
        ConstraintLayout operatoreLayout;
        TextView userMessage, userDate, userHour;
        TextView operatoreMessage, operatoreDate, operatoreHour;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userLayout = itemView.findViewById(R.id.userMessageLayout);
            userMessage = itemView.findViewById(R.id.userMessageTextView);
            userDate = itemView.findViewById(R.id.userDataTextView);
            userHour = itemView.findViewById(R.id.userOraTextView);
            operatoreLayout = itemView.findViewById(R.id.operatoreMessageLayout);
            operatoreMessage = itemView.findViewById(R.id.operatoreMessageTextView);
            operatoreDate = itemView.findViewById(R.id.operatoreDataTextView);
            operatoreHour = itemView.findViewById(R.id.operatoreOraTextView);
        }
    }


    private void saveAnswer(String risposta, String identificativo) {
        // variabili
        boolean operatore = MainActivity.CURRENTUSER.isOperatore();

        Date time = new Date();
        int hour = time.getHours();
        int minute= time.getMinutes();
        int second = time.getSeconds();

        CalendarDay currentDate = CalendarDay.today();
        int day = currentDate.getDay();
        int month = currentDate.getMonth();
        int year = currentDate.getYear();

        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fStore.collection("tickets").document(identificativo)
                .collection("messages").document();

        Message message = new Message(risposta,day,month,year,hour,minute,second,operatore);

        documentReference.set(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mFirestoreList.smoothScrollToPosition(mFirestoreList.getAdapter().getItemCount() - 1);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }

    //start&stop listening
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // ends this activity (back arrow)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}