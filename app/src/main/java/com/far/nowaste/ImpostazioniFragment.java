package com.far.nowaste;

import android.os.Bundle;

import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ImpostazioniFragment extends PreferenceFragmentCompat {

    // dichiarazione view
    Preference mLoginPreference;
    EditTextPreference mFullNamePreference;
    Preference mPicturePreference;
    EditTextPreference mEmailPreference;
    EditTextPreference mPasswordPreference;
    Preference mLogOutPreference;
    EditTextPreference mResetPreference;
    EditTextPreference mDeletePreference;
    SwitchPreferenceCompat mNotificationPreference;
    ListPreference mThemePreference;
    Preference mVersionePreference;

    // firebase
    FirebaseAuth fAuth;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

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

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() == null) {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mPicturePreference.setVisible(false);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
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

    @Override
    public void onStart() {
        super.onStart();
        if (fAuth.getCurrentUser() == null) {
            mLoginPreference.setVisible(true);
            mFullNamePreference.setVisible(false);
            mPicturePreference.setVisible(false);
            mEmailPreference.setVisible(false);
            mPasswordPreference.setVisible(false);
            mLogOutPreference.setVisible(false);
            mResetPreference.setVisible(false);
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
}
