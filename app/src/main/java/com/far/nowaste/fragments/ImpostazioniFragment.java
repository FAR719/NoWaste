package com.far.nowaste.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.far.nowaste.BuildConfig;
import com.far.nowaste.MainActivity;
import com.far.nowaste.R;
import com.far.nowaste.objects.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ImpostazioniFragment extends PreferenceFragmentCompat {

    // dichiarazione view
    Preference mLoginPreference, mFullNamePreference, mNewPicturePreference, mDeletePicturePreference,
            mCityPreference, mQuartierePreference, mEmailPreference, mPasswordPreference,
            mLogOutPreference, mResetPreference, mDeletePreference, mOperatorePreference,
            mThemePreference, mVersionePreference;
    SwitchPreferenceCompat mNotificationPreference;

    // firebase
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseStorage storage;
    StorageReference storageReference;

    final String operatoreKey = "a1b2c3d4e5";

    // custom dialog layout
    LinearLayout.LayoutParams mainParams;

    // imageUri
    Uri imageUri;

    // variabili night mode
    int nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // assegna le view
        mLoginPreference = findPreference("login_preference");
        mFullNamePreference = findPreference("full_name_preference");
        mNewPicturePreference = findPreference("newpicture_preference");
        mDeletePicturePreference = findPreference("deletepicture_preference");
        mCityPreference = findPreference("city_preference");
        mQuartierePreference = findPreference("quartiere_preference");
        mEmailPreference = findPreference("email_preference");
        mPasswordPreference = findPreference("password_preference");
        mLogOutPreference = findPreference("logout_preference");
        mResetPreference = findPreference("reset_preference");
        mDeletePreference = findPreference("delete_preference");
        mOperatorePreference = findPreference("operatore_preference");
        mNotificationPreference = findPreference("notification_preference");
        mThemePreference = findPreference("theme_preference");
        mVersionePreference = findPreference("version_preference");

        // set custom dialogs layout params
        mainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

        setVisiblePreferences();
        setupPreferences();
    }

    private void setVisiblePreferences(){
        if (MainActivity.CURRENTUSER == null) {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mNewPicturePreference.setVisible(false);
            mDeletePicturePreference.setVisible(false);
            mCityPreference.setVisible(false);
            mQuartierePreference.setVisible(false);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
            mDeletePreference.setVisible(false);
            mOperatorePreference.setVisible(false);
        } else if (MainActivity.CURRENTUSER.isGoogle()) {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mNewPicturePreference.setVisible(true);
            mDeletePicturePreference.setVisible(true);
            mCityPreference.setVisible(true);
            mQuartierePreference.setVisible(!MainActivity.CURRENTUSER.getCity().equals(""));
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(false);
            mOperatorePreference.setVisible(!MainActivity.CURRENTUSER.isOperatore());
        } else {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mNewPicturePreference.setVisible(true);
            mDeletePicturePreference.setVisible(true);
            mCityPreference.setVisible(true);
            mQuartierePreference.setVisible(!MainActivity.CURRENTUSER.getCity().equals(""));
            mEmailPreference.setVisible(true);
            mPasswordPreference.setVisible(true);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(true);
            mOperatorePreference.setVisible(!MainActivity.CURRENTUSER.isOperatore());
        }
    }

    private void setupPreferences(){
        // set login
        mLoginPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity)getActivity()).goToLogin();
                return true;
            }
        });

        // set new name
        mFullNamePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_full_name, mainLayout, false);

                // declare fullNameEditText
                TextInputEditText fullNameInput = layout2.findViewById(R.id.fullNameDialog_nameInputText);

                mainLayout.addView(layout2);

                // set dialog
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Nome");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFullName = fullNameInput.getText().toString().trim();
                        String oldName = fUser.getDisplayName();
                        if (!newFullName.equals(oldName)){
                            // update main and firestore
                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            Map<String, Object> nameMap = new HashMap<>();
                            nameMap.put("fullName", newFullName);
                            fStore.collection("users").document(fUser.getUid()).update(nameMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // update fAuth
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newFullName).build();
                                    fUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                MainActivity.CURRENTUSER.setFullName(newFullName);
                                                // update the nav_header
                                                ((MainActivity)getActivity()).updateHeader();
                                                ((MainActivity)getActivity()).showSnackbar("Nome aggiornato!");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        // upload the proPic
        mNewPicturePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
                return true;
            }
        });

        // delete the proPic
        mDeletePicturePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Elimina profilo");
                builder.setMessage("Vuoi eliminare le foto del tuo profilo?");
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        storageReference = storage.getReference();
                        final String key = MainActivity.CURRENTUSER.getEmail();
                        StorageReference picRef = storageReference.child("proPics/" + key);
                        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                                Map<String, Object> imageMap = new HashMap<>();
                                imageMap.put("image", null);
                                fStore.collection("users").document(fUser.getUid()).update(imageMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                MainActivity.CURRENTUSER.setImage(null);
                                                ((MainActivity)getActivity()).updateHeader();
                                                ((MainActivity)getActivity()).showSnackbar("La foto del tuo profilo è stata eliminata!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(getContext(), "Error! " + exception.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.show();
                return true;
            }
        });

        // set the city
        mCityPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_city, mainLayout, false);

                // declare radioButtons
                RadioGroup mRadioGroup = layout2.findViewById(R.id.cityRadioGroup);
                RadioButton mBarlettaRadioBtn = layout2.findViewById(R.id.barlettaRadioButton);
                RadioButton mBariRadioBtn = layout2.findViewById(R.id.bariRadioButton);

                if (MainActivity.CURRENTUSER.getCity() != null) {
                    switch (MainActivity.CURRENTUSER.getCity()){
                        case "Barletta":
                            mRadioGroup.check(R.id.barlettaRadioButton);
                            break;
                        case "Bari":
                            mRadioGroup.check(R.id.bariRadioButton);
                            break;
                    }
                }

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Città");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> cityMap = new HashMap<>();
                        if (mBarlettaRadioBtn.isChecked()) {
                            if (!MainActivity.CURRENTUSER.getCity().equals("Barletta")) {
                                cityMap.put("city", "Barletta");
                                cityMap.put("quartiere", "");
                            }
                        } else if (mBariRadioBtn.isChecked()) {
                            if (!MainActivity.CURRENTUSER.getCity().equals("Bari")) {
                                cityMap.put("city", "Bari");
                                cityMap.put("quartiere", "");
                            }
                        }
                        if (cityMap.get("city") != null) {
                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            fStore.collection("users").document(fAuth.getUid()).update(cityMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    MainActivity.CURRENTUSER.setCity(cityMap.get("city").toString());
                                    MainActivity.CURRENTUSER.setQuartiere("");
                                    mQuartierePreference.setVisible(true);
                                    ((MainActivity)getActivity()).showSnackbar("Città impostata: " + cityMap.get("city"));
                                }
                            });

                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        // set the neighborhood
        mQuartierePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_quartiere, mainLayout, false);

                // declare radioButtons
                RadioGroup mRadioGroup = layout2.findViewById(R.id.quartiereRadioGroup);
                RadioButton mQ1RadioBtn = layout2.findViewById(R.id.q1RadioButton);
                RadioButton mQ2RadioBtn = layout2.findViewById(R.id.q2RadioButton);

                switch (MainActivity.CURRENTUSER.getCity()){
                    case "Barletta":
                        mQ1RadioBtn.setText("Borgovilla");
                        mQ2RadioBtn.setText("Patalini");
                        if (MainActivity.CURRENTUSER.getQuartiere() != null) {
                            switch (MainActivity.CURRENTUSER.getQuartiere()){
                                case "Borgovilla":
                                    mRadioGroup.check(R.id.q1RadioButton);
                                    break;
                                case "Patalini":
                                    mRadioGroup.check(R.id.q2RadioButton);
                                    break;
                            }
                        }
                        break;
                    case "Bari":
                        mQ1RadioBtn.setText("Santo Spirito");
                        mQ2RadioBtn.setText("Zona industriale");
                        if (MainActivity.CURRENTUSER.getQuartiere() != null) {
                            switch (MainActivity.CURRENTUSER.getQuartiere()){
                                case "Santo Spirito":
                                    mRadioGroup.check(R.id.q1RadioButton);
                                    break;
                                case "Zona Industriale":
                                    mRadioGroup.check(R.id.q2RadioButton);
                                    break;
                            }
                        }
                        break;
                }

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Quartiere");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> quartMap = new HashMap<>();
                        switch (MainActivity.CURRENTUSER.getCity()){
                            case "Barletta":
                                if (mQ1RadioBtn.isChecked()) {
                                    quartMap.put("quartiere", "Borgovilla");
                                } else if (mQ2RadioBtn.isChecked()) {
                                    quartMap.put("quartiere", "Patalini");
                                }
                                break;
                            case "Bari":
                                if (mQ1RadioBtn.isChecked()) {
                                    quartMap.put("quartiere", "Santo Spirito");
                                } else if (mQ2RadioBtn.isChecked()) {
                                    quartMap.put("quartiere", "Zona industriale");
                                }
                                break;
                        }
                        if (quartMap.get("quartiere") != null) {
                            FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                            fStore.collection("users").document(fAuth.getUid()).update(quartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    MainActivity.CURRENTUSER.setQuartiere(quartMap.get("quartiere").toString());
                                    ((MainActivity)getActivity()).showSnackbar("Quartiere impostato: " + quartMap.get("quartiere"));
                                }
                            });
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        // set new email
        mEmailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_email, mainLayout, false);

                // declare and set passEditText and emailEditText
                TextInputEditText emailInput = layout2.findViewById(R.id.emailDialog_emailInputText);
                TextInputEditText passInput = layout2.findViewById(R.id.emailDialog_passwordInputText);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Email");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).changeEmail(passInput.getText().toString().trim(), emailInput.getText().toString().trim());
                    }
                });
                builder.show();
                return true;
            }
        });

        // set new password
        mPasswordPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_password, mainLayout, false);

                // declare and set oldPassEditText and newPassEditText
                TextInputEditText oldPassInput = layout2.findViewById(R.id.passwordDialog_oldPasswordInputText);
                TextInputEditText newPassInput = layout2.findViewById(R.id.passwordDialog_newPasswordInputText);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Password");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).changePassword(oldPassInput.getText().toString().trim(), newPassInput.getText().toString().trim());
                    }
                });
                builder.show();
                return true;
            }
        });

        // logout
        mLogOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Logout");
                builder.setMessage("Vuoi uscire dal tuo account?");
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).logout();
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Reset dati");
                builder.setMessage("Vuoi eliminare i dati del tuo account? Tale operazione è irreversibile!");
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
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
                        if (MainActivity.CURRENTUSER != null) {
                            Utente utente = new Utente(MainActivity.CURRENTUSER.getFullName(),
                                    MainActivity.CURRENTUSER.getEmail(), MainActivity.CURRENTUSER.getImage(),
                                    MainActivity.CURRENTUSER.isGoogle(), MainActivity.CURRENTUSER.isOperatore(),
                                    MainActivity.CURRENTUSER.getCity(), MainActivity.CURRENTUSER.getQuartiere());
                            MainActivity.CURRENTUSER = utente;
                        }
                        dialog.dismiss();
                        ((MainActivity)getActivity()).showSnackbar("Reset dei dati eseguito!");
                    }
                });
                builder.show();
                return true;
            }
        });

        // delete account
        mDeletePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_delete, mainLayout, false);

                // declare and set emailText and passEditText
                TextInputEditText passInput = layout2.findViewById(R.id.deleteDialog_passwordInputText);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Elimina account");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).deleteAccount(passInput.getText().toString().trim());
                    }
                });
                builder.show();
                return true;
            }
        });

        mOperatorePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_key_operatore, mainLayout, false);

                // declare keyEditText
                TextInputEditText keyInput = layout2.findViewById(R.id.operatoreDialog_keyInputText);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Operatore ecologico");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                        String writtenKey = keyInput.getText().toString().trim();
                        if (writtenKey.length() != 10) {
                            ((MainActivity)getActivity()).showSnackbar("La chiave deve essere lunga 10 caratteri!");
                        } else if (!writtenKey.equals(operatoreKey)) {
                            ((MainActivity)getActivity()).showSnackbar("La chiave inserita non è valida!");
                        } else {
                            // aggiorna profilo Firestore
                            Map<String, Object> operatoreMappa = new HashMap<>();
                            operatoreMappa.put("isOperatore", true);
                            fStore.collection("users").document(fUser.getUid()).update(operatoreMappa)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // aggiorna variabile in main
                                            MainActivity.CURRENTUSER.setOperatore(true);
                                            mOperatorePreference.setVisible(false);
                                            ((MainActivity)getActivity()).showSnackbar("Il tuo account aziendale è stato attivato!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "Error! " + e.toString());
                                }
                            });
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        // set themePreference summary
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            mThemePreference.setSummary("Chiaro");
        } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            mThemePreference.setSummary("Scuro");
        } else {
            mThemePreference.setSummary("Predefinito di sistema");
        }

        // change theme
        mThemePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_theme, mainLayout, false);

                // declare radioButtons
                RadioGroup mRadioGroup = layout2.findViewById(R.id.themeRadioGroup);
                RadioButton mChiaroRadioBtn = layout2.findViewById(R.id.chiaroRadioButton);
                RadioButton mScuroRadioBtn = layout2.findViewById(R.id.scuroRadioButton);
                RadioButton mDefaultRadioBtn = layout2.findViewById(R.id.defaultRadioButton);

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    mRadioGroup.check(R.id.chiaroRadioButton);
                } else if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    mRadioGroup.check(R.id.scuroRadioButton);
                } else {
                    mRadioGroup.check(R.id.defaultRadioButton);
                }

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
                builder.setTitle("Tema");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mChiaroRadioBtn.isChecked()) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            mThemePreference.setSummary("Chiaro");

                            nightMode = AppCompatDelegate.getDefaultNightMode();
                            sharedPreferences = getActivity().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("NightModeInt", nightMode);
                            editor.apply();
                        } else if (mScuroRadioBtn.isChecked()) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            mThemePreference.setSummary("Scuro");

                            nightMode = AppCompatDelegate.getDefaultNightMode();
                            sharedPreferences = getActivity().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("NightModeInt", nightMode);
                            editor.apply();
                        } else if (mDefaultRadioBtn.isChecked()) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            mThemePreference.setSummary("Predefinito di sistema");

                            nightMode = AppCompatDelegate.getDefaultNightMode();
                            sharedPreferences = getActivity().getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);
                            editor = sharedPreferences.edit();

                            editor.putInt("NightModeInt", nightMode);
                            editor.apply();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        // show version number
        mVersionePreference.setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(getContext(), R.style.ThemeOverlay_NoWaste_AlertDialog);
        pd.setTitle("Caricamento dell'immagine...");
        pd.show();

        final String key = MainActivity.CURRENTUSER.getEmail();
        StorageReference pictureRef = storageReference.child("proPics/" + key);

        pictureRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> downloadUri = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                updateFireStoreAuth(uri.toString(), pd);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pd.dismiss();
                        Toast.makeText(getContext(), "Errore di caricamento." + exception.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Progressi: " + (int)progressPercent + "%");
            }
        });
    }

    private void updateFireStoreAuth(String picUrl, ProgressDialog pd) {
        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("image", picUrl);
        // upload inFireAuth and FireStore
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(Uri.parse(picUrl)).build();

        fUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                fStore.collection("users").document(fUser.getUid()).update(imageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.CURRENTUSER.setImage(picUrl);
                        ((MainActivity)getActivity()).updateHeader();
                        pd.dismiss();
                        ((MainActivity)getActivity()).showSnackbar("La foto del tuo profilo è stata cambiata correttamente!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getContext(), "Error! " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}