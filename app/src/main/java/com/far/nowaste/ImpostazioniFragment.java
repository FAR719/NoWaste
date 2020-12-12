package com.far.nowaste;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class ImpostazioniFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
