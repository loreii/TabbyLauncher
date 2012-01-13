/**
 * 
 */
package org.tabbylauncher.component;

import org.tabbylauncher.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author fabiobenedetti
 *
 */
public class StackLayoutBar extends LinearLayout {

	LayoutInflater mInflater;
    private View mButton;

	/**
	 * @param context
	 */
	public StackLayoutBar(Context context) {
		super(context);
		initLayout();
		}

	/**
	 * @param context
	 * @param attrs
	 */
	public StackLayoutBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLayout();
	}

	 private void initLayout() {
	        mInflater = LayoutInflater.from(getContext());
	        mButton = mInflater.inflate(R.layout.all_applications_button, this, false);
	        addView(mButton);

	        setBackgroundDrawable(null);
	        setWillNotDraw(false);
	    }

	
}
