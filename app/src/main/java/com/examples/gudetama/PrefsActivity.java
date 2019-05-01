package com.examples.gudetama;

import android.os.Build;
import android.preference.PreferenceActivity;

import java.util.List;

public class PrefsActivity extends PreferenceActivity {
    @Override
    protected boolean isValidFragment (String fragmentName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            return true;
        else if (PrefsFragmentSettings.class.getName().equals(fragmentName))
            return true;
        return false;
    }
    @Override
    public void onBuildHeaders (List<Header> target) {
        // Use this to load an XML file containing references to multiple fragments (a multi-screen preferences screen)
        // loadHeadersFromResource(R.xml.prefs_headers, target);

        // Use this to load an XML file containing a single preferences screen
        getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragmentSettings()).commit();
    }
}