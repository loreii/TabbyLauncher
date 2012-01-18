/**
 * 
 */
package org.tabbylauncher.fragment;

import java.util.ArrayList;
import java.util.List;

import org.tabbylauncher.ApplicationInfo;
import org.tabbylauncher.R;
import org.tabbylauncher.component.StackLayoutBar;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author fabiobenedetti
 *
 */
public class HomeBar extends Fragment {

	StackLayoutBar mStackLayoutBar;
	LayoutInflater mInflater;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mInflater = inflater;
		return inflater.inflate(R.layout.stacklayoutbar, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		View fragment = getView();
		mStackLayoutBar = (StackLayoutBar) fragment.findViewById(R.id.stacklayoutbar);

//		setElement(getResources().getDrawable(R.drawable.all_applications), "All App");
		
		FragmentActivity fragmentActivity = getActivity();
		Context context = fragmentActivity.getApplicationContext();
		bindRecents(context);
	}

    private void bindRecents(Context context) {
        final PackageManager manager = getActivity().getPackageManager();
        final ActivityManager tasksManager = (ActivityManager) getActivity().getSystemService(context.ACTIVITY_SERVICE);
        final List<ActivityManager.RecentTaskInfo> recentTasks = tasksManager.getRecentTasks(20, 0);

        final int count = recentTasks.size();
        final ArrayList<ApplicationInfo> recents = new ArrayList<ApplicationInfo>();

        for (int i = count - 1; i >= 0; i--) {
        
            final Intent intent = recentTasks.get(i).baseIntent;

            if (Intent.ACTION_MAIN.equals(intent.getAction()) &&
                    !intent.hasCategory(Intent.CATEGORY_HOME)) {

                ApplicationInfo info = getApplicationInfo(manager, intent);
                if (info != null) {
                    info.intent = intent;            		
                    setElement(info.icon, (String) info.title);
                }
            }
        }

//        mApplicationsStack.setRecents(recents);
    }
    
    
    private void setElement(Drawable drawable, String text){
		View view = mInflater.inflate(R.layout.all_applications_button, null);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		image.setImageDrawable(drawable);
		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(text);
		mStackLayoutBar.addView(view);

    }
    
    private static ApplicationInfo getApplicationInfo(PackageManager manager, Intent intent) {
        final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);

        if (resolveInfo == null) {
            return null;
        }

        final ApplicationInfo info = new ApplicationInfo();
        final ActivityInfo activityInfo = resolveInfo.activityInfo;
        info.icon = activityInfo.loadIcon(manager);
        if (info.title == null || info.title.length() == 0) {
            info.title = activityInfo.loadLabel(manager);
        }
        if (info.title == null) {
            info.title = "";
        }
        return info;
    }
	
}
