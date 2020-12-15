package com.far.nowaste;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class TicketChatActivity extends AppCompatActivity {
    // definizione variabili
    Toolbar mToolbar;
    RecyclerView mFirestoreList;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_chat);

        // toolbar
        mToolbar = findViewById(R.id.ticketChat_toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary));

        // back arrow
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String identificativo = intent.getStringExtra("com.far.nowaste.identificativo");

        // recyclerView + FireBase
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        mFirestoreList = findViewById(R.id.ticketChat_recyclerView);

        if(fAuth.getCurrentUser() == null){
            finish();
        }

        // query
        Query query = firebaseFirestore.collection("tickets").document(identificativo).collection("messaggi");

        // recyclerOptions
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();

        adapter = new FirestoreRecyclerAdapter<Message, TicketChatActivity.ChatViewHolder>(options) {
            @NonNull
            @Override
            public TicketChatActivity.ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_chat_message_item_layout, parent, false);
                return new TicketChatActivity.ChatViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TicketChatActivity.ChatViewHolder holder, int position, @NonNull Message model) {
                holder.rMessaggio.setText(model.getDescrizione());
                //holder.rData.setText(model.getDay() + "/" + model.getMonth() + "/" + model.getYear());
            }
        };


        // View Holder
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);

        // divider nella recyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mFirestoreList.addItemDecoration(dividerItemDecoration);
    }


    private class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView rMessaggio;
        ConstraintLayout itemLayout;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            rMessaggio = itemView.findViewById(R.id.recView_chatMessItem_oggettoTextView);
            itemLayout = itemView.findViewById(R.id.recView_chatMessItem_constrainLayout);
        }
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