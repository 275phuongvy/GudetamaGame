package com.examples.gudetama;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

public class PrefsFragmentSettings extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    //Context context;
    public PrefsFragmentSettings() {
    }
    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preference from an XML resource
        addPreferencesFromResource(R.xml.prefs_fragment_settings);
        //this.context = this;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener when a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        // Set up a click listener for company info
        Preference pref;
        pref = getPreferenceScreen().findPreference("key_author_info");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick (Preference preference) {
                //Handle action on click here
                try {
                    Uri site = Uri.parse("https://www.linkedin.com/in/vy-tran-12bb93134/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, site);
                    startActivity(intent);
                }
                catch (Exception e) {
                    Log.e("PrefsFragmentSettings", "Browser failed", e);
                }
                return true;
            }
        } );

        pref = getPreferenceScreen().findPreference("key_new_game");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick (Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Are you sure you want to start new game?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Assets.state = Assets.GameState.Starting;
                                //setPreferenceScreen(null);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                return true;
            }
        } );
    }


    public void onSharedPreferenceChanged (SharedPreferences sharedPreferences, String key) {
        if (key.equals("key_music_enabled")) {
            boolean b = sharedPreferences.getBoolean("key_music_enabled", true);
            if (b==false) {
                if (Assets.mp != null)
                    Assets.mp.setVolume(0, 0);
            }
            else {
                if (Assets.mp != null)
                    Assets.mp.setVolume(1, 1);
            }
        }
    }
}
