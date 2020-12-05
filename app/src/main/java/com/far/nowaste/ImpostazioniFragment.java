package com.far.nowaste;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class ImpostazioniFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
