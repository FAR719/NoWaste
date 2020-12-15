package com.far.nowaste;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ImpostazioniFragment extends PreferenceFragmentCompat {

    // dichiarazione view
    Preference mLoginPreference;
    EditTextPreference mFullNamePreference;
    Preference mPicturePreference;
    EditTextPreference mPasswordPreference;
    Preference mLogOutPreference;
    Preference mResetPreference;
    Preference mDeletePreference;
    SwitchPreferenceCompat mNotificationPreference;
    ListPreference mThemePreference;
    Preference mVersionePreference;

    // firebase
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    Dialog dialog;

    // se 1 esegui il logout
    static int impNum;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        impNum = 0;

        fStore = FirebaseFirestore.getInstance();

        // assegna le view
        mLoginPreference = findPreference("login_preference");
        mFullNamePreference = findPreference("full_name_preference");
        mPicturePreference = findPreference("picture_preference");
        mPasswordPreference = findPreference("password_preference");
        mLogOutPreference = findPreference("logout_preference");
        mResetPreference = findPreference("reset_preference");
        mDeletePreference = findPreference("delete_preference");
        mNotificationPreference = findPreference("notification_preference");
        mThemePreference = findPreference("theme_preference");
        mVersionePreference = findPreference("version_preference");

        setVisiblePreferences();

        dialog = new Dialog(getContext());

        mLogOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                logoutDialog();
                return true;
            }
        });

        mResetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                resetDialog();
                return true;
            }
        });
    }

    private void setVisiblePreferences(){
        fAuth = FirebaseAuth.getInstance();
        if (fAuth.getCurrentUser() != null) {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mPicturePreference.setVisible(true);
            mPasswordPreference.setVisible(true);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(true);
        } else {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mPicturePreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
            mDeletePreference.setVisible(false);
        }
    }

    // Reset Dialog
    private void resetDialog(){
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView mTitle = dialog.findViewById(R.id.dialog_title);
        TextView mMessage = dialog.findViewById(R.id.dialog_message);
        TextView mNeutral = dialog.findViewById(R.id.dialog_neutral_button);
        TextView mPositive = dialog.findViewById(R.id.dialog_positive_button);
        dialog.show();
        mTitle.setText("Reset dati");
        mMessage.setText("Vuoi cancellare i dati del tuo account? Tale operazione è irreversibile!");
        mNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            dialog.dismiss();
            }
        });
        mPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> mappa = new HashMap<>();
                mappa.put("nPlastica", 0);
                mappa.put("pPlastica", 0);
                mappa.put("nOrganico", 0);
                mappa.put("pOrganico", 0);
                mappa.put("nIndifferenziata", 0);
                mappa.put("pIndifferenziata", 0);
                mappa.put("nCarta", 0);
                mappa.put("pCarta", 0);
                mappa.put("nVetro", 0);
                mappa.put("pVetro", 0);
                mappa.put("nMetalli", 0);
                mappa.put("pMetalli", 0);
                mappa.put("nElettrici", 0);
                mappa.put("pElettrici", 0);
                mappa.put("nSpeciali", 0);
                mappa.put("pSpeciali", 0);
                fStore.collection("users").document(fAuth.getCurrentUser().getUid()).update(mappa);
                dialog.dismiss();
                Toast.makeText(getContext(), "Reset dei dati eseguito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Logout Dialog
    private void logoutDialog(){
        dialog.setContentView(R.layout.dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView mTitle = dialog.findViewById(R.id.dialog_title);
        TextView mMessage = dialog.findViewById(R.id.dialog_message);
        TextView mNeutral = dialog.findViewById(R.id.dialog_neutral_button);
        TextView mPositive = dialog.findViewById(R.id.dialog_positive_button);
        dialog.show();
        mTitle.setText("Logout");
        mMessage.setText("Vuoi uscire dal tuo account?");
        mNeutral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        mPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impNum = 1;
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });
    }
}
