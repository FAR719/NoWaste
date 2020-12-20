package com.far.nowaste.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.far.nowaste.BuildConfig;
import com.far.nowaste.LoginActivity;
import com.far.nowaste.MainActivity;
import com.far.nowaste.R;
import com.far.nowaste.Objects.Utente;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ImpostazioniFragment extends PreferenceFragmentCompat {

    // dichiarazione view
    Preference mLoginPreference;
    Preference mFullNamePreference;
    Preference mNewPicturePreference;
    Preference mDeletePicturePreference;
    Preference mEmailPreference;
    Preference mPasswordPreference;
    Preference mLogOutPreference;
    Preference mResetPreference;
    Preference mDeletePreference;
    Preference mOperatorePreference;
    SwitchPreferenceCompat mNotificationPreference;
    ListPreference mThemePreference;
    Preference mVersionePreference;

    // firebase
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseStorage storage;
    StorageReference storageReference;

    Utente currentUser;

    final String operatoreKey = "a1b2c3d4e5";

    // custom dialog layout
    LinearLayout.LayoutParams mainParams;

    // imageUri
    Uri imageUri;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        currentUser = MainActivity.CURRENTUSER;

        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // assegna le view
        mLoginPreference = findPreference("login_preference");
        mFullNamePreference = findPreference("full_name_preference");
        mNewPicturePreference = findPreference("newpicture_preference");
        mDeletePicturePreference = findPreference("deletepicture_preference");
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
        if (currentUser == null) {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mNewPicturePreference.setVisible(false);
            mDeletePicturePreference.setVisible(false);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
            mDeletePreference.setVisible(false);
            mOperatorePreference.setVisible(false);
        } else if (currentUser.isGoogle()) {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mNewPicturePreference.setVisible(true);
            mDeletePicturePreference.setVisible(true);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(false);
            mOperatorePreference.setVisible(!currentUser.isOperatore());
        } else {
            mLoginPreference.setVisible(false);
            mFullNamePreference.setVisible(true);
            mNewPicturePreference.setVisible(true);
            mDeletePicturePreference.setVisible(true);
            mEmailPreference.setVisible(true);
            mPasswordPreference.setVisible(true);
            mLogOutPreference.setVisible(true);
            mResetPreference.setVisible(true);
            mDeletePreference.setVisible(true);
            mOperatorePreference.setVisible(!currentUser.isOperatore());
        }
    }

    private void setupPreferences(){
        // set login
        mLoginPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity)getActivity()).launchLogin();
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
                EditText fullNameEditText = layout2.findViewById(R.id.fullNameDialog_editTextFullName);

                mainLayout.addView(layout2);

                // set dialog
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Nome");
                builder.setMessage("Inserisci il tuo nome:");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFullName = fullNameEditText.getText().toString().trim();
                        if (!newFullName.equals(fUser.getDisplayName())){
                            // update main and firestore
                            MainActivity.CURRENTUSER.setFullName(newFullName);
                            fStore.collection("users").document(fUser.getUid()).set(MainActivity.CURRENTUSER);

                            // update fAuth
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(newFullName).build();
                            fUser.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "User profile updated.");
                                    }
                                }
                            });

                            // update the nav_header
                            ((MainActivity)getActivity()).updateHeader();

                            Toast.makeText(getContext(), "Nome aggiornato.", Toast.LENGTH_SHORT).show();
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
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
                        final String key = currentUser.getEmail();
                        StorageReference picRef = storageReference.child("proPics/" + key);
                        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fStore = FirebaseFirestore.getInstance();
                                Map<String, Object> imageMap = new HashMap<>();
                                imageMap.put("image", null);
                                fStore.collection("users").document(fUser.getUid()).update(imageMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                MainActivity.CURRENTUSER.setImage(null);
                                                currentUser = MainActivity.CURRENTUSER;
                                                ((MainActivity)getActivity()).updateHeader();
                                                Toast.makeText(getContext(), "Immagine eliminata!", Toast.LENGTH_SHORT).show();
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

        // set new email
        mEmailPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // main layout
                LinearLayout mainLayout = new LinearLayout(getContext());
                mainLayout.setLayoutParams(mainParams);

                // inflate the layout
                View layout2 = LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_email, mainLayout, false);

                // declare and set emailText, passEditText and emailEditText
                EditText passEditText = layout2.findViewById(R.id.emailDialog_editTextPassword);
                EditText emailEditText = layout2.findViewById(R.id.emailDialog_editTextEmail);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Email");
                builder.setMessage("Inserisci la tua nuova email e la password corrente:");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).changeEmail(passEditText.getText().toString().trim(), emailEditText.getText().toString().trim());
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

                // declare and set emailText, oldPassEditText and newPassEditText
                TextView emailView = layout2.findViewById(R.id.passwordDialog_emailTextView);
                emailView.setText(MainActivity.CURRENTUSER.getEmail());
                EditText oldPassEditText = layout2.findViewById(R.id.passwordDialog_editTextOldPassword);
                EditText newPassEditText = layout2.findViewById(R.id.passwordDialog_editTextNewPassword);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Password");
                builder.setMessage("Inserisci la tua nuova password:");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).changePassword(oldPassEditText.getText().toString().trim(), newPassEditText.getText().toString().trim());
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
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
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Reset dati");
                builder.setMessage("Vuoi cancellare i dati del tuo account? Tale operazione è irreversibile!");
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
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
                        if (MainActivity.CURRENTUSER != null) {
                            Utente utente = new Utente(MainActivity.CURRENTUSER.getFullName(), MainActivity.CURRENTUSER.email, MainActivity.CURRENTUSER.getImage(), MainActivity.CURRENTUSER.isGoogle(), MainActivity.CURRENTUSER.isOperatore());
                            MainActivity.CURRENTUSER = utente;
                        }
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Reset dei dati eseguito", Toast.LENGTH_SHORT).show();
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
                TextView emailView = layout2.findViewById(R.id.deleteDialog_emailTextView);
                emailView.setText(MainActivity.CURRENTUSER.getEmail());
                EditText passEditText = layout2.findViewById(R.id.deleteDialog_editTextPassword);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Elimina account");
                builder.setMessage("Per cancellare il tuo account inserisci nuovamente la password:");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((MainActivity)getActivity()).deleteAccount(passEditText.getText().toString().trim());
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

                // declare and set emailText and passEditText
                EditText keyEditText = layout2.findViewById(R.id.keyOperatoreDialog_editTextKey);

                mainLayout.addView(layout2);

                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme);
                builder.setTitle("Operatore ecologico");
                builder.setMessage("Inserisci la chiave di attivazione fornita dalla tua azienda.");
                builder.setView(mainLayout);
                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fStore = FirebaseFirestore.getInstance();
                        String writtenKey = keyEditText.getText().toString().trim();
                        if (writtenKey.length() != 10) {
                            Toast.makeText(getContext(), "La chiave deve essere lunga 10 caratteri.", Toast.LENGTH_SHORT).show();
                        } else if (!writtenKey.equals(operatoreKey)) {
                            Toast.makeText(getContext(), "La chiave inserita non è valida.", Toast.LENGTH_SHORT).show();
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
                                            currentUser = MainActivity.CURRENTUSER;
                                            mOperatorePreference.setVisible(false);
                                            Toast.makeText(getContext(), "Il tuo account operatore è stato attivato!", Toast.LENGTH_SHORT).show();
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
        final ProgressDialog pd = new ProgressDialog(getContext(), R.style.AlertDialogTheme);
        pd.setTitle("Caricamento dell'immagine...");
        pd.show();

        final String key = currentUser.getEmail();
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
                fStore = FirebaseFirestore.getInstance();
                fStore.collection("users").document(fUser.getUid()).update(imageMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        MainActivity.CURRENTUSER.setImage(picUrl);
                        ((MainActivity)getActivity()).updateHeader();
                        pd.dismiss();
                        Toast.makeText(getContext(), "Immagine cambiata.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

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