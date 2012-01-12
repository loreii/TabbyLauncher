package org.tabbylauncher;

import java.util.List;

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
	}


}
