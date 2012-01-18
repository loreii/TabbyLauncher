/**
 * 
 */
package org.tabbylauncher.component;

import java.util.List;

import org.tabbylauncher.ApplicationInfo;
import org.tabbylauncher.R;
import org.tabbylauncher.Rotor;
import org.tabbylauncher.Rotor.OnItemSelectedListener;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
	OnItemSelectedListener  mOnItemSelectedListener; 
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


		setApplicationsInfo(context.getResources().getDrawable(R.drawable.phone_icon), 
				"Call",
				new Intent(Intent.ACTION_DIAL));

		setApplicationsInfo(context.getResources().getDrawable(R.drawable.contacts_icon), 
				"Message",
				new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI));

		setApplicationsInfo(context.getResources().getDrawable(R.drawable.cat_icon), 
				"All",
				null);

		setApplicationsInfo(context.getResources().getDrawable(R.drawable.message_icon), 
				"Search",
				new Intent(Intent.ACTION_VIEW, Uri.parse("sms:")));

		setApplicationsInfo(context.getResources().getDrawable(R.drawable.camera_icon), 
				"Add",
				new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE));


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
		titleview.setVisibility(View.GONE);
//		titleview.setText(applicationInfo.title);
		view.setTag(applicationInfo.intent);
		view.setOnClickListener(this);
		this.addView(view);
	}

	@Override
	public void onClick(View view) {
		Object object = view.getTag();
		if(object != null){
			getContext().startActivity((Intent) object);
		}else{
			mOnItemSelectedListener.onItemSelected();
		}


	}
	
	public synchronized void setOnItemSelectedListener(OnItemSelectedListener listener) {
		this.mOnItemSelectedListener=listener;
	}

	public static interface OnItemSelectedListener {
		public void onItemSelected();
	}


}
