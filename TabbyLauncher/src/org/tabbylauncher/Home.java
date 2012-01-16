package org.tabbylauncher;

import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class Home extends FragmentActivity {
	TextView mApplicationNameTextView;
	Rotor mRotor;

	// Identifiers for option menu items
	private static final int MENU_WALLPAPER_SETTINGS = Menu.FIRST + 1;
	private static final int MENU_TAG = MENU_WALLPAPER_SETTINGS + 1;
	private static final int MENU_SETTINGS = MENU_TAG + 1;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mApplicationNameTextView = (TextView) findViewById(R.id.application_name_text_view);
		mRotor = (Rotor) findViewById(R.id.rotor);
		mRotor.setOnItemSelectedListener(new Rotor.OnItemSelectedListener() {
			@Override
			public void onItemSelected(Rotor rotor, List<ApplicationInfo> appList,
					ApplicationInfo appInfo, int index) {
				mApplicationNameTextView.setText(appInfo.title);
			}
		});
		mRotor.setOnItemClickListener(new Rotor.OnRotorClickListener() {
			@Override
			public void onItemClick(Rotor rotor, List<ApplicationInfo> appList,
					ApplicationInfo appInfo, int index) {
				startActivity(appInfo.intent);
			}

			@Override
			public void onQuadrantListener(int idx, Intent intent) {
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_WALLPAPER_SETTINGS, 0, R.string.menu_wallpaper)
		.setIcon(android.R.drawable.ic_menu_gallery)
		.setAlphabeticShortcut('W');
		menu.add(0, MENU_TAG, 0, R.string.menu_tag)
		.setIcon(android.R.drawable.btn_star)
		.setAlphabeticShortcut(SearchManager.MENU_KEY);


		menu.add(0, MENU_SETTINGS, 0, R.string.menu_settings)
		.setIcon(android.R.drawable.ic_menu_preferences)
		.setIntent(new Intent(android.provider.Settings.ACTION_SETTINGS));

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_WALLPAPER_SETTINGS:
			startWallpaper();
			return true;
		case MENU_TAG:
			startTagging();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void startTagging() {
		final Intent tagI = new Intent(this,TagListActivity.class);
		startActivity(tagI);

	}

	private void startWallpaper() {
		final Intent pickWallpaper = new Intent(Intent.ACTION_SET_WALLPAPER);
		startActivity(Intent.createChooser(pickWallpaper, getString(R.string.menu_wallpaper)));
	}

}
