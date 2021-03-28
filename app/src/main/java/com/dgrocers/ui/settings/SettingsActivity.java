package com.dgrocers.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.dgrocers.R;
import com.dgrocers.firebase.AccountManager;
import com.dgrocers.ui.login.LoginActivity;

public class SettingsActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.settings, new SettingsFragment())
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class SettingsFragment extends PreferenceFragmentCompat {
		@Override
		public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
			setPreferencesFromResource(R.xml.root_preferences, rootKey);
			findPreference("pref_current_login").setSummary("Logged in as " + AccountManager.getInstance().getCurrentLoggedInAdmin().getName());
			findPreference("pref_logout").setOnPreferenceClickListener(preference -> {
				AccountManager.getInstance().logoutAdmin();
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
				return true;
			});
		}
	}
}