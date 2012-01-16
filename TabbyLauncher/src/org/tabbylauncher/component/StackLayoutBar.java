/**
 * 
 */
package org.tabbylauncher.component;

import org.tabbylauncher.ApplicationInfo;
import org.tabbylauncher.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author fabiobenedetti
 *
 */
public class StackLayoutBar extends LinearLayout implements OnClickListener{

	LayoutInflater mInflater;

	/**
	 * @param context
	 */
	public StackLayoutBar(Context context) {
		super(context);
		initLayout(context);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public StackLayoutBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout(context);
	}

	private void initLayout(Context context) {
		mInflater = LayoutInflater.from(getContext());
		
		
		setApplicationsInfo(context.getResources().getDrawable(android.R.drawable.ic_menu_call), 
				"Call",
				new Intent(Intent.ACTION_DIAL));
		
		setApplicationsInfo(context.getResources().getDrawable(android.R.drawable.ic_menu_send), 
				"Message",
				new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI));
		
		setApplicationsInfo(context.getResources().getDrawable(android.R.drawable.ic_dialog_dialer), 
				"All",
				new Intent(Intent.ACTION_MAIN));
		
		setApplicationsInfo(context.getResources().getDrawable(android.R.drawable.ic_menu_search), 
				"Search",
				new Intent(Intent.ACTION_SEARCH));

		setApplicationsInfo(context.getResources().getDrawable(android.R.drawable.ic_menu_add), 
				"Add",
				new Intent());


		setBackgroundDrawable(null);
		setWillNotDraw(false);
	}

	private void setApplicationsInfo(Drawable drawable, String title, Intent intent){
		ApplicationInfo applicationInfo = new ApplicationInfo();
		applicationInfo.icon = drawable;
		applicationInfo.title = title;
		applicationInfo.intent = intent;
		addIcon(applicationInfo);
	}
	
	private void addIcon(ApplicationInfo applicationInfo) {
		View view = mInflater.inflate(R.layout.all_applications_button, this, false);
		ImageView imageview = (ImageView) view.findViewById(R.id.image);
		imageview.setImageDrawable(applicationInfo.icon);
		TextView titleview = (TextView) view.findViewById(R.id.title);
		titleview.setText(applicationInfo.title);
		view.setTag(applicationInfo.intent);
		view.setOnClickListener(this);
		this.addView(view);
	}

	@Override
	public void onClick(View view) {
		getContext().startActivity((Intent) view.getTag());
		
		
	}


}
