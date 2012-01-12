package org.tabbylauncher;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

public class Home extends FragmentActivity {
	TextView mApplicationNameTextView;
	Rotor mRotor;


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
}
