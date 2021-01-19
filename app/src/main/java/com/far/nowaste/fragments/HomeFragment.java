package com.far.nowaste.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.far.nowaste.CategoriaActivity;
import com.far.nowaste.MainActivity;
import com.far.nowaste.objects.Evento;
import com.far.nowaste.objects.Settimanale;
import com.far.nowaste.objects.Utente;
import com.far.nowaste.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class HomeFragment extends Fragment {

    MaterialCardView seccoCardView, plasticaCardView, cartaCardView, organicoCardView, vetroCardView,
            metalliCardView, elettriciCardView, specialiCardView, raccoltaCardView, eventCardView;
    ConstraintLayout quartiereLayout;
    TextView quartiere, lunedi1, lunedi2, lunediE, martedi1, martedi2, martediE, mercoledi1, mercoledi2,
            mercolediE, giovedi1, giovedi2, giovediE, venerdi1, venerdi2, venerdiE, sabato, domenica,
            warningTextView, eventoTitle, eventoBody;

    // firebase
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fAuth = FirebaseAuth.getInstance();

        // collega le view
        seccoCardView = view.findViewById(R.id.indifferenziataCardView);
        plasticaCardView = view.findViewById(R.id.plasticaCardView);
        cartaCardView = view.findViewById(R.id.cartaCardView);
        organicoCardView = view.findViewById(R.id.organicoCardView);
        vetroCardView = view.findViewById(R.id.vetroCardView);
        metalliCardView = view.findViewById(R.id.metalliCardView);
        elettriciCardView = view.findViewById(R.id.elettriciCardView);
        specialiCardView = view.findViewById(R.id.specialiCardView);

        raccoltaCardView = view.findViewById(R.id.raccoltaCardView);
        eventCardView = view.findViewById(R.id.home_eventCardView);
        warningTextView = view.findViewById(R.id.home_warning);

        quartiereLayout = view.findViewById(R.id.quartiere_layout);

        eventoTitle = view.findViewById(R.id.home_eventTitle);
        eventoBody = view.findViewById(R.id.home_eventBody);

        quartiere = view.findViewById(R.id.quartiere_conferimento);
        lunedi1 = view.findViewById(R.id.lun_1conferimento);
        lunedi2 = view.findViewById(R.id.lun_2conferimento);
        lunediE = view.findViewById(R.id.lun_e);
        martedi1 = view.findViewById(R.id.mar_1conferimento);
        martedi2 = view.findViewById(R.id.mar_2conferimento);
        martediE = view.findViewById(R.id.mar_e);
        mercoledi1 = view.findViewById(R.id.mer_1conferimento);
        mercoledi2 = view.findViewById(R.id.mer_2conferimento);
        mercolediE = view.findViewById(R.id.mer_e);
        giovedi1 = view.findViewById(R.id.gio_1conferimento);
        giovedi2 = view.findViewById(R.id.gio_2conferimento);
        giovediE = view.findViewById(R.id.gio_e);
        venerdi1 = view.findViewById(R.id.ven_1conferimento);
        venerdi2 = view.findViewById(R.id.ven_2conferimento);
        venerdiE = view.findViewById(R.id.ven_e);
        sabato = view.findViewById(R.id.sab_conferimento);
        domenica = view.findViewById(R.id.dom_conferimento);

        raccoltaCardView.setVisibility(View.GONE);
        quartiereLayout.setVisibility(View.GONE);
        eventCardView.setVisibility(View.GONE);

        // definizione onClick cardView
        clickCard(plasticaCardView, "Plastica", 0);
        clickCard(organicoCardView, "Organico", 1);
        clickCard(seccoCardView,"Secco", 2);
        clickCard(cartaCardView, "Carta", 3);
        clickCard(vetroCardView, "Vetro", 4);
        clickCard(metalliCardView,"Metalli", 5);
        clickCard(elettriciCardView, "Elettrici", 6);
        clickCard(specialiCardView, "Speciali", 7);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCardData();
    }

    private void updateCardData() {
        if (MainActivity.CURRENT_USER == null && fAuth.getCurrentUser() != null) {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("users").document(fAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Utente currentuser = documentSnapshot.toObject(Utente.class);
                    setQuartiereCard(currentuser);
                    setEventCard(currentuser);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        } else if (MainActivity.CURRENT_USER != null) {
            setQuartiereCard(MainActivity.CURRENT_USER);
            setEventCard(MainActivity.CURRENT_USER);
        } else {
            raccoltaCardView.setVisibility(View.VISIBLE);
            quartiereLayout.setVisibility(View.GONE);
            warningTextView.setVisibility(View.VISIBLE);
            eventCardView.setVisibility(View.GONE);
            warningTextView.setText("Accedi per visualizzare il calendario della raccolta settimanale");
            raccoltaCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).goToLogin();
                }
            });
        }
    }

    private void setQuartiereCard(Utente user) {
        if (user.getCity().equals("")) {
            raccoltaCardView.setVisibility(View.VISIBLE);
            quartiereLayout.setVisibility(View.GONE);
            warningTextView.setVisibility(View.VISIBLE);
            warningTextView.setText("Imposta la tua città ed il tuo quartiere dalle impostazioni per visualizzare il calendario della raccolta settimanale");
            raccoltaCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).goToSettings();
                }
            });
        } else if (user.getQuartiere().equals("")){
            raccoltaCardView.setVisibility(View.VISIBLE);
            quartiereLayout.setVisibility(View.GONE);
            warningTextView.setVisibility(View.VISIBLE);
            warningTextView.setText("Imposta il tuo quartiere dalle impostazioni per visualizzare il calendario della raccolta settimanale");
            raccoltaCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).goToSettings();
                }
            });
        } else {
            raccoltaCardView.setVisibility(View.VISIBLE);
            quartiereLayout.setVisibility(View.VISIBLE);
            warningTextView.setVisibility(View.GONE);
            String userQuartiere = user.getQuartiere();
            quartiere.setText("Quartiere " + userQuartiere);
            loadSettimanale(user);
        }
    }

    private void loadSettimanale(Utente user) {
        if (MainActivity.SETTIMANALE != null) {
            setDayViews(MainActivity.SETTIMANALE);
        } else {
            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
            fStore.collection("settimanale").document(user.getQuartiere()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    MainActivity.SETTIMANALE = documentSnapshot.toObject(Settimanale.class);
                    setDayViews(MainActivity.SETTIMANALE);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("LOG", "Error! " + e.getLocalizedMessage());
                }
            });
        }
    }

    private void setEventCard(Utente user) {
        CalendarDay currentDay = CalendarDay.today();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("events").whereEqualTo("email", user.getEmail())
                .whereEqualTo("year", currentDay.getYear()).whereEqualTo("month", currentDay.getMonth())
                .whereEqualTo("day", currentDay.getDay()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    eventCardView.setVisibility(View.GONE);
                } else {
                    Evento evento = queryDocumentSnapshots.getDocuments().get(0).toObject(Evento.class);
                    eventCardView.setVisibility(View.VISIBLE);
                    eventoTitle.setText(evento.getTitle());
                    eventoBody.setText(evento.getDescription());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                eventCardView.setVisibility(View.GONE);
                Log.e("LOG", "Error! " + e.getLocalizedMessage());
            }
        });
    }

    // definizione metodo per onClickCardView (valido per ogni carta)
    private void clickCard(View view, String string, int ntipo) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listaCardActivity = new Intent(getContext(), CategoriaActivity.class);
                listaCardActivity.putExtra("com.far.nowaste.CATEGORIA", string);
                listaCardActivity.putExtra("com.far.nowaste.NCATEGORIA", ntipo);
                startActivity(listaCardActivity);
            }
        });
    }

    private void setDayViews(Settimanale settimanale){
        lunedi1.setText(settimanale.getLunedi1());
        setViewColor(lunedi1);
        if (!settimanale.getLunedi2().equals("")) {
            lunediE.setVisibility(View.VISIBLE);
            lunediE.setText(" e ");
            lunedi2.setVisibility(View.VISIBLE);
            lunedi2.setText(settimanale.getLunedi2());
            setViewColor(lunedi2);
        } else {
            lunediE.setVisibility(View.GONE);
            lunedi2.setVisibility(View.GONE);
        }

        martedi1.setText(settimanale.getMartedi1());
        setViewColor(martedi1);
        if (!settimanale.getMartedi2().equals("")) {
            martediE.setVisibility(View.VISIBLE);
            martediE.setText(" e ");
            martedi2.setVisibility(View.VISIBLE);
            martedi2.setText(settimanale.getMartedi2());
            setViewColor(martedi2);
        } else {
            martediE.setVisibility(View.GONE);
            martedi2.setVisibility(View.GONE);
        }

        mercoledi1.setText(settimanale.getMercoledi1());
        setViewColor(mercoledi1);
        if (!settimanale.getMercoledi2().equals("")) {
            mercolediE.setVisibility(View.VISIBLE);
            mercolediE.setText(" e ");
            mercoledi2.setVisibility(View.VISIBLE);
            mercoledi2.setText(settimanale.getMercoledi2());
            setViewColor(mercoledi2);
        } else {
            mercolediE.setVisibility(View.GONE);
            mercoledi2.setVisibility(View.GONE);
        }

        giovedi1.setText(settimanale.getGiovedi1());
        setViewColor(giovedi1);
        if (!settimanale.getGiovedi2().equals("")) {
            giovediE.setVisibility(View.VISIBLE);
            giovediE.setText(" e ");
            giovedi2.setVisibility(View.VISIBLE);
            giovedi2.setText(settimanale.getGiovedi2());
            setViewColor(giovedi2);
        } else {
            giovediE.setVisibility(View.GONE);
            giovedi2.setVisibility(View.GONE);
        }

        venerdi1.setText(settimanale.getVenerdi1());
        setViewColor(venerdi1);
        if (!settimanale.getVenerdi2().equals("")) {
            venerdiE.setVisibility(View.VISIBLE);
            venerdiE.setText(" e ");
            venerdi2.setVisibility(View.VISIBLE);
            venerdi2.setText(settimanale.getVenerdi2());
            setViewColor(venerdi2);
        } else {
            venerdiE.setVisibility(View.GONE);
            venerdi2.setVisibility(View.GONE);
        }

        sabato.setText(settimanale.getSabato());
        setViewColor(sabato);

        domenica.setText(settimanale.getDomenica());
        setViewColor(domenica);
    }

    private void setViewColor(TextView textView){
        switch (textView.getText().toString()) {
            case "Plastica":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.plastica_home));
                break;
            case "Organico":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.organico_home));
                break;
            case "Secco":
                textView.setTextColor(ContextCompat.getColor(textView.getContext(), R.color.secco_home));
                break;
        }
    }
}
