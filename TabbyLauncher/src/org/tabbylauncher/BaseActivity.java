/**
 * 
 */
package org.tabbylauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

/**
 * @author fabiobenedetti
 *
 */
public class BaseActivity extends FragmentActivity {
	protected ArrayList<ApplicationInfo> mApplications = new ArrayList<ApplicationInfo>();
	protected Thread mApplicationLoaderThread=null;
	private Handler mHandler;


	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mHandler = new Handler();
	}
	
	
	protected void loadApplications(boolean isLaunching, final Rotor mRotor) {
		if (mApplicationLoaderThread==null) {
			mApplicationLoaderThread = new Thread("ApplicationLoader") {
				public void run() {
					PackageManager manager = getBaseContext().getPackageManager();

					Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
					mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

					final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
					Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
					ArrayList<ApplicationInfo> appInfos=null;
					if (apps != null) {
						final int count = apps.size();

						appInfos = new ArrayList<ApplicationInfo>(count);
						for (int i = 0; i < count; i++) {
							ApplicationInfo application = new ApplicationInfo();
							ResolveInfo info = apps.get(i);
							application.title = info.loadLabel(manager);
							application.pakage = info.activityInfo.applicationInfo.packageName;
							application.setActivity(new ComponentName(
									info.activityInfo.applicationInfo.packageName,
									info.activityInfo.name),
									Intent.FLAG_ACTIVITY_NEW_TASK
									| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
							application.icon = info.activityInfo.loadIcon(manager);
							appInfos.add(application);
						}
					}
					synchronized (mApplications) {
						final boolean changed = appInfos==null||
								mApplications.size()!=appInfos.size();
						mApplications.clear();
						if (appInfos!=null) {
							int i = 0;																	//TODO REMOVE ONLY FOR PREVIEW
							for (ApplicationInfo info : appInfos) {
								info.color=ColorUtils.color[i];i=(i+1) % ColorUtils.color.length;		//TODO REMOVE ONLY FOR PREVIEW
								mApplications.add(info);
							}
						}
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mRotor.onApplicationsLoadingFinished(changed);
							}
						});
						mApplicationLoaderThread=null;
						mRotor.updateSectors();
						mRotor.applicationReady=true;
					}
				}
			};
			mApplicationLoaderThread.start();
		}
	}

}
