package com.far.nowaste.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.far.nowaste.AssistenzaActivity;
import com.far.nowaste.NewBugActivity;
import com.far.nowaste.MainActivity;
import com.far.nowaste.R;
import com.far.nowaste.TabTicketActivity;
import com.far.nowaste.objects.Azienda;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

public class ContattaciFragment extends Fragment {

    MaterialCardView ticketCard, contattaciCard, assistenzaCard;
    TextView nomeAzienda, indirizzoAzienda, telefonoAzienda, emailAzienda, lun, mar, mer, gio, ven, sab, dom;
    Button callBtn, emailBtn;

    final Azienda Barsa = new Azienda("Barsa SPA", "Via Callano, 61, 76121 Barletta BT",
            "0883 304215", "info@barsa.it", "8.30-13, 15-18", "8.30-13, 15-18",
            "8.30-13, 15-18", "8.30-13, 15-18", "8.30-13, 15-18", "8.30-13", "Chiuso");
    final Azienda Amiu = new Azienda("Amiu Puglia SPA", "Via Napoli, 349, 70123 Bari BA",
            "800-011558", "segreteria.amiu@legalmail.it", "7.30-12", "7.30-12",
            "7.30-12", "7.30-12", "7.30-12", "7.30-12", "Chiuso");

    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contattaci, container, false);

        fAuth = FirebaseAuth.getInstance();

        ticketCard = view.findViewById(R.id.contattaci_ticketCard);
        contattaciCard = view.findViewById(R.id.contattaci_contattaciCard);
        assistenzaCard = view.findViewById(R.id.assistenza_MaterialCard);

        callBtn = view.findViewById(R.id.callBtn);
        emailBtn = view.findViewById(R.id.emailBtn);

        // textViews azienda
        nomeAzienda = view.findViewById(R.id.nome_azienda);
        indirizzoAzienda = view.findViewById(R.id.indirizzo_azienda);
        telefonoAzienda = view.findViewById(R.id.telefono_azienda);
        emailAzienda = view.findViewById(R.id.email_azienda);
        lun = view.findViewById(R.id.lunAzienda);
        mar = view.findViewById(R.id.marAzienda);
        mer = view.findViewById(R.id.merAzienda);
        gio = view.findViewById(R.id.gioAzienda);
        ven = view.findViewById(R.id.venAzienda);
        sab = view.findViewById(R.id.sabAzienda);
        dom = view.findViewById(R.id.domAzienda);

        ticketCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fAuth.getCurrentUser() != null) {
                    startActivity(new Intent(getContext(), TabTicketActivity.class));
                } else {
                    ((MainActivity)getActivity()).showSnackbar("Accedi per inviare ticket!");
                }
            }
        });

        if (MainActivity.CURRENT_USER == null || MainActivity.CURRENT_USER.getCity().equals("")) {
            contattaciCard.setVisibility(View.GONE);
        } else if (MainActivity.CURRENT_USER.getCity().equals("Bari")) {
            setContattaciCardData(Amiu);
        } else if (MainActivity.CURRENT_USER.getCity().equals("Barletta")) {
            setContattaciCardData(Barsa);
        }

        assistenzaCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AssistenzaActivity.class));
            }
        });

        return view;
    }

    private void setContattaciCardData(Azienda azienda) {
        nomeAzienda.setText(azienda.getNome());
        telefonoAzienda.setText(azienda.getTelefono());
        emailAzienda.setText(azienda.getEmail());
        indirizzoAzienda.setText(azienda.getIndirizzo());
        lun.setText(azienda.getLun());
        mar.setText(azienda.getMar());
        mer.setText(azienda.getMer());
        gio.setText(azienda.getGio());
        ven.setText(azienda.getVen());
        sab.setText(azienda.getSab());
        dom.setText(azienda.getDom());

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + azienda.getTelefono()));
                startActivity(intent);
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients = {azienda.getEmail()};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.setType("text/html");
                startActivity(Intent.createChooser(intent, "Invia una mail"));
            }
        });
    }
}
