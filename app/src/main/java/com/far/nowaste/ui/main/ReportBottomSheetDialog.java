package com.far.nowaste.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.far.nowaste.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ReportBottomSheetDialog extends BottomSheetDialogFragment {

    TextView mTitolo, mCassonetto, mEmail, mIndirizzo, mCommento, mData;
    boolean isOperatore;
    String titolo, cassonetto, email, indirizzo, commento;
    int year, month, day;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_report_bottom_sheet, container, false);

        mTitolo = view.findViewById(R.id.reportSheet_title);
        mCassonetto = view.findViewById(R.id.reportSheet_cassonetto);
        mEmail = view.findViewById(R.id.reportSheet_email);
        mIndirizzo = view.findViewById(R.id.reportSheet_indirizzo);
        mCommento = view.findViewById(R.id.reportSheet_commento);
        mData = view.findViewById(R.id.reportSheet_data);

        isOperatore = this.getArguments().getBoolean("isOperatore");
        titolo = this.getArguments().getString("titolo");
        cassonetto = this.getArguments().getString("cassonetto");
        email = this.getArguments().getString("email");
        indirizzo = this.getArguments().getString("indirizzo");
        commento = this.getArguments().getString("commento");
        year = this.getArguments().getInt("year");
        month = this.getArguments().getInt("month");
        day = this.getArguments().getInt("day");
        setData();

        return view;
    }

    private void setData() {
        mTitolo.setText(titolo);
        mCassonetto.setText("Tipologia: " + cassonetto);
        if (isOperatore) {
            mEmail.setVisibility(View.VISIBLE);
            mEmail.setText(email);
        } else {
            mEmail.setVisibility(View.GONE);
        }
        mIndirizzo.setText(indirizzo);
        if (commento.equals("")) {
            mCommento.setVisibility(View.GONE);
        } else {
            mCommento.setVisibility(View.VISIBLE);
            mCommento.setText(commento);
        }
        String sDay, sMonth;
        if (day < 10) {
            sDay = "0" + day;
        } else {
            sDay = day + "";
        }
        if (month < 10) {
            sMonth = "0" + month;
        } else  {
            sMonth = month + "";
        }
        mData.setText(sDay + "/" + sMonth + "/" + year);
    }
}
