package com.far.nowaste.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.far.nowaste.BuildConfig;
import com.far.nowaste.LoginActivity;
import com.far.nowaste.MainActivity;
import com.far.nowaste.R;
import com.far.nowaste.Objects.Utente;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ImpostazioniFragment extends PreferenceFragmentCompat {

    // dichiarazione view
    Preference mLoginPreference;
    EditTextPreference mFullNamePreference;
    Preference mPicturePreference;
    EditTextPreference mEmailPreference;
    EditTextPreference mPasswordPreference;
    Preference mLogOutPreference;
    Preference mResetPreference;
    EditTextPreference mDeletePreference;
    SwitchPreferenceCompat mNotificationPreference;
    ListPreference mThemePreference;
    Preference mVersionePreference;

    // firebase
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;

    Utente currentUser;

    // se 1 logout, se 2 elimina account
    static public int IMPNUM;

    // password inserita per la ri-autenticazione
    static public String PASSWORD;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        IMPNUM = 0;
        currentUser = MainActivity.CURRENTUSER;

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();

        // assegna le view
        mLoginPreference = findPreference("login_preference");
        mFullNamePreference = findPreference("full_name_preference");
        mPicturePreference = findPreference("picture_preference");
        mEmailPreference = findPreference("email_preference");
        mPasswordPreference = findPreference("password_preference");
        mLogOutPreference = findPreference("logout_preference");
        mResetPreference = findPreference("reset_preference");
        mDeletePreference = findPreference("delete_preference");
        mNotificationPreference = findPreference("notification_preference");
        mThemePreference = findPreference("theme_preference");
        mVersionePreference = findPreference("version_preference");

        setVisiblePreferences();
        setupPreferences();
    }

    private void setVisiblePreferences(){
        if (currentUser == null) {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mPicturePreference.setVisible(false);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
            mDeletePreference.setVisible(false);
        } else if (currentUser.isGoogle()) {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mPicturePreference.setVisible(false);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(false);
        } else {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mPicturePreference.setVisible(true);
            mEmailPreference.setVisible(true);
            mPasswordPreference.setVisible(true);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(true);
        }
    }

    private void loadSetting(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        String newName = sharedPreferences.getString("full_name_preference", currentUser.getFullName());
        String theme = sharedPreferences.getString("theme_preference", "0");
    }

    private void setupPreferences(){
        // set new name


        // logout
        mLogOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Logout");
                builder.setMessage("Vuoi uscire dal tuo account?");
                builder.setNeutralButton("Indietro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IMPNUM = 1;
                        startActivity(new Intent(getContext(), LoginActivity.class));
                    }
                });
                builder.show();
                return true;
            }
        });

        // reset data
        mResetPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Reset dati");
                builder.setMessage("Vuoi cancellare i dati del tuo account? Tale operazione è irreversibile!");
                builder.setNeutralButton("Indietro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                        fStore.collection("users").document(fUser.getUid()).update(mappa);
                        fStore.collection("users").document(fUser.getUid()).update(mappa);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Reset dei dati eseguito", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            }
        });

        // delete account
        /*mDeletePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Elimina account");
                builder.setMessage("Vuoi cancellare il tuo account? Tale operazione è irreversibile!");
                builder.setNeutralButton("Indietro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        IMPNUM = 2;
                        startActivity(new Intent(getContext(), LoginActivity.class));
                    }
                });
                builder.show();
                return true;
            }
        });*/

        mVersionePreference.setSummary(BuildConfig.VERSION_NAME);
        mVersionePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
                        .replace(R.id.main_frameLayout, new HomeFragment()).commit();
                MainActivity.FRAGMENT = 1;
                return true;
            }
        });
    }
}

/*@Override
    public void onStart() {
        super.onStart();
        setVisiblePreferences();
    }

    private void setVisiblePreferences(){
        if (fAuth.getCurrentUser() != null) {
            fStore.collection("users").document(fUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.getBoolean("isGoogle")) {
                        mLoginPreference.setVisible(false);
                        mFullNamePreference.setVisible(true);
                        mPicturePreference.setVisible(false);
                        mPasswordPreference.setVisible(false);
                        mLogOutPreference.setVisible(true);
                        mResetPreference.setVisible(true);
                        mDeletePreference.setVisible(false);
                    } else {
                        mLoginPreference.setVisible(false);
                        mFullNamePreference.setVisible(true);
                        mPicturePreference.setVisible(true);
                        mPasswordPreference.setVisible(true);
                        mLogOutPreference.setVisible(true);
                        mResetPreference.setVisible(true);
                        mDeletePreference.setVisible(true);
                    }
                }
            });
        } else {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mPicturePreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
            mDeletePreference.setVisible(false);
        }
    }*/