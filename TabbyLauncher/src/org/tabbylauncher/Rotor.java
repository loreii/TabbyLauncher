package org.tabbylauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class Rotor extends SurfaceView implements SurfaceHolder.Callback  {
	/*
	 * ============ Update animation thread =============
	 */

	class AnimationThread extends Thread {

		private long 		  mTimer;
		private SurfaceHolder mSurfaceHolder;
		private Handler 	  mHandler;
		private Context 	  mContext;

		//TODO handle start/stop
		private boolean 	 mRun=true;		
		private float mCircleRadius;
		private float mCircleExtRadius;
		private int mCircleWidth;
		private Paint mLinePaint;
		private int mTouchAlpha;
		private int mCircleRed=0;
		private int mCircleBlue=0;
		private int mCircleGreen=255;

		public AnimationThread(SurfaceHolder surfaceHolder, Context context,
				Handler handler) {

			// get handles to some important objects
			mSurfaceHolder = surfaceHolder;
			mHandler 	   = handler;
			mContext 	   = context;

			// Initialize paints for finger hit
			mLinePaint = new Paint();
			mLinePaint.setAntiAlias(true);
			mLinePaint.setARGB(255, 0, 255, 0);
			mLinePaint.setTextSize(15f);
		}



		/**
		 * Restores view state
		 * 
		 * @param savedState Bundle containing the last state
		 */
		public synchronized void restoreState(Bundle savedState) {
			synchronized (mSurfaceHolder) {
				//TODO
			}
		}

		@Override
		public void run() {

			while (mRun) {
				Canvas c = null;
				try {
					c = mSurfaceHolder.lockCanvas(null);
					if (c!=null) {
						synchronized (mSurfaceHolder) {
							updatePhysics();
							doDraw(c);
							angleIcon(c);
						}
					} else {
						mRun=false;
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
				try {
					sleep(50);
				} catch (InterruptedException e) {
					mRun=false;
				}
			}

		}




		private void angleIcon(Canvas canvas) {


			ApplicationInfo applicationInfo = mFavorites.get(0);
			Bitmap bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
			canvas.drawBitmap(bitmap, 10, 10, null);

			applicationInfo = mFavorites.get(1);
			bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
			canvas.drawBitmap(bitmap, mCanvasWidth-bitmap.getWidth(), 10, null);

			applicationInfo = mFavorites.get(2);
			bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
			canvas.drawBitmap(bitmap, 10, mCanvasHeight-bitmap.getHeight(), null);

			applicationInfo = mFavorites.get(3);
			bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
			canvas.drawBitmap(bitmap, mCanvasWidth-bitmap.getWidth(), mCanvasHeight-bitmap.getHeight(), null);

		}



		/**
		 * Make the drawing
		 * */
		private void doDraw(Canvas canvas) {
			canvas.save();

			mLinePaint.setARGB(255, 0, 0,0);
			canvas.drawRect(0, 0, mCanvasWidth, mCanvasHeight, mLinePaint);

			if (mTouchAlpha>0) {
				mTouchAlpha-=10;
				if (mTouchAlpha>0)
					mLinePaint.setARGB(mTouchAlpha, 0, 0, 255);
				canvas.drawCircle(touchDownX, touchDownY, 20, mLinePaint);
			}
			//mLinePaint.setARGB(255, 255, 0, 0);
			//canvas.drawText("##("+lambda+")",20, 20, mLinePaint);



			//mLinePaint.setARGB(255, 0, 0, 255);
			//canvas.drawLine(touchDownX, touchDownY,mCanvasWidth/2, mCanvasHeight/2, mLinePaint);
			//canvas.drawLine(touchDownX, touchDownY,touchDownX, mCanvasHeight/2, mLinePaint);
			//canvas.drawLine(touchDownX, mCanvasHeight/2,mCanvasWidth/2, mCanvasHeight/2, mLinePaint);

			mLinePaint.setARGB(255, mCircleRed, mCircleGreen, mCircleBlue);
			mLinePaint.setStyle(Style.STROKE);
			mLinePaint.setStrokeWidth(mCircleWidth);
			canvas.drawCircle(mCenterX, mCenterY, mCircleRadius, mLinePaint);
			mLinePaint.setStyle(Style.FILL);

			//			mLinePaint.setARGB(255, 0, 0, 0);
			//			canvas.drawCircle(mXCenter, mYCenter, mXCenter - 30, mLinePaint);

			mLinePaint.setARGB(255, 255, 255, 255);

				synchronized (mApplications) {
					if (mSelectedApp>=0) {
						ApplicationInfo app = mApplications.get(mSelectedApp);
						Bitmap bitmap = ((BitmapDrawable)app.icon).getBitmap();
						Matrix mtx = new Matrix();

						RectF drawableRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
						RectF viewRect = new RectF(0, 0, 200, 200);
						mtx.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

						Bitmap scaledBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
						int bitmapWidth  = (scaledBMP.getWidth()>>1);
						int bitmapHeight = (scaledBMP.getHeight()>>1);

						canvas.drawBitmap(scaledBMP,(mCanvasWidth/2)-bitmapWidth, (mCanvasHeight/2)-bitmapHeight, null);
					}
				}

			canvas.restore();
		}

		/**
		 * Update physics animation
		 * */
		private void updatePhysics() {
			// TODO Auto-generated method stub

		}

		/* Callback invoked when the surface dimensions change. */
		public void setSurfaceSize(int width, int height) {
			// synchronized to make sure these all change atomically
			synchronized (mSurfaceHolder) {
				mCanvasWidth = width;
				mCanvasHeight = height;
				mCenterX=width>>1;
				mCenterY=height>>1;
				mCircleExtRadius = Math.min(width, height)/2.0f;
				mCircleWidth = (int)mCircleExtRadius/3;
				mCircleRadius = mCircleExtRadius-(mCircleWidth>>1);
			}

		}

		protected void onTouch() {
			synchronized (mSurfaceHolder) {
				mTouchAlpha=255;
				mCircleRed=mCircleRed>2?mCircleRed-2:255;
				mCircleGreen=mCircleGreen>1?mCircleGreen-1:255;
				mCircleBlue=mCircleBlue>4?mCircleBlue-4:255;
			}
		}

	}


	private Context  mContext;
	private Handler mHandler;
	private Vibrator vibrator;
	private org.tabbylauncher.Rotor.AnimationThread thread;
	private int prevX;
	private int prevY;
	private double oldLambda;
	private double lambda;
	private int touchDownX;
	private int touchDownY;
	private long downtime;
	private boolean inversion;
	private double rotation;
	private boolean refresh;
	private Thread mApplicationLoaderThread=null;
	private ArrayList<ApplicationInfo> mApplications = new ArrayList<ApplicationInfo>();
	private int mSelectedApp=-1;
	private ArrayList<ApplicationInfo> mFavorites;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private int mCenterX;
	private int mCenterY;
	private OnItemSelectedListener mOnItemSelectedListener;


	/*
	 * View Handler 
	 */

	public Rotor(Context context) {
		super(context);
		mHandler = new Handler();
	}


	public Rotor(Context context, AttributeSet attrs) {
		super(context, attrs);


		mContext = context;
		mHandler = new Handler();

		// register our interest in hearing about changes to our surface
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		// create thread only; it's started in surfaceCreated()
		thread = new AnimationThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {}
		});


		setFocusable(true); // make sure we get key events

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		loadFavorites(mContext);
		loadApplications(true);
	}

	private void loadFavorites(Context context) {

		if(mFavorites==null){
			mFavorites = new ArrayList<ApplicationInfo>(4);
		}
		mFavorites.clear();
		ApplicationInfo applicationInfo = new ApplicationInfo();
		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_call);
		applicationInfo.title = "Phone";
		mFavorites.add(applicationInfo);
		applicationInfo = new ApplicationInfo();
		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_send);
		applicationInfo.title = "Message";
		mFavorites.add(applicationInfo);
		applicationInfo = new ApplicationInfo();
		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_my_calendar);
		applicationInfo.title = "Calendar";
		mFavorites.add(applicationInfo);
		applicationInfo = new ApplicationInfo();
		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_agenda);
		applicationInfo.title = "Agenda";
		mFavorites.add(applicationInfo);
	}


	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		//		if (!hasWindowFocus) thread.pause();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

		thread.setSurfaceSize(width, height);
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// added fix -->
		if(thread.getState()== Thread.State.TERMINATED){
			thread  = new AnimationThread(holder, mContext, new Handler() {
				@Override
				public void handleMessage(Message m) {}
			});

			thread.start();
			// <-- added fix
		}else {
			thread.start();
		}
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		synchronized ( event ){
			int i = -1;
			switch (event.getActionMasked()){
			case MotionEvent.ACTION_DOWN:

				Log.d("DEBUG", "get action down");
				prevX=(int)event.getX();
				prevY=(int)event.getY();
				i=0;
				break;
			case MotionEvent.ACTION_UP:

				Log.d("DEBUG", "release action up");
				oldLambda=lambda;
				lambda=0;
				i=1; 
				break;
			default:
				break;
			}


			//update the current touch location
			touchDownX= (int)event.getX();
			touchDownY= (int)event.getY();

			downtime=event.getDownTime();

			//
			//			double a = angleFromPoint(prevX,prevY,mCanvasWidth,mCanvasHeight);
			//			double b = angleFromPoint(touchDownX,touchDownY,mCanvasWidth,mCanvasHeight);
			//
			//
			//
			//			double lambda2 = a-b;
			//
			//			if((lambda2<0 && lambda > 0 )||(lambda2>0 && lambda < 0 )){
			//				//				vibrator.vibrate(202);
			//				inversion=true;
			//			}else{
			//				inversion=false;
			//			}
			//
			//
			//
			//			if(lambda2!=0){
			//				lambda = lambda2;
			//				refresh=true;
			//
			//				rotation += lambda2;
			//
			//				if((rotation % 1)==0 && inversion==false){
			//					vibrator.vibrate(2);
			//				}
			//			}

			int b = (int) angleFromPoint(touchDownX,touchDownY,mCanvasWidth,mCanvasHeight);
			synchronized (mApplications) {
				int oldSelected = mSelectedApp;
				mSelectedApp = Math.abs(b)%mApplications.size();
				if (mOnItemSelectedListener!=null && oldSelected!=mSelectedApp) {
					mOnItemSelectedListener.onItemSelected(this, mApplications, 
							mApplications.get(mSelectedApp), mSelectedApp);
				}
			}

			prevX=touchDownX;
			prevY=touchDownY;
			thread.onTouch();
		}


		return true;
	}    

	private static double angleFromPoint(int x, int y, int width, int height) {
		double r = Math.atan2(x - (width >>1 ), (height >> 1) - y);
		return  (int) Math.toDegrees(r);
	}

	public void onApplicationsLoadingFinished(boolean changed) {
		int oldSelected=mSelectedApp;
		if (mApplications.isEmpty()) {
			mSelectedApp=-1;
		} else if (changed) {
			mSelectedApp=0;
		}
		if (oldSelected!=mSelectedApp && mOnItemSelectedListener!=null) {
			mOnItemSelectedListener.onItemSelected(this, mApplications, 
					mApplications.get(mSelectedApp), mSelectedApp);
		}
		this.invalidate();
	}

	private void loadApplications(boolean isLaunching) {
		if (mApplicationLoaderThread==null) {
			mApplicationLoaderThread = new Thread("ApplicationLoader") {
				public void run() {
					PackageManager manager = getContext().getPackageManager();

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
							for (ApplicationInfo info : appInfos) {
								mApplications.add(info);
							}
						}
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								onApplicationsLoadingFinished(changed);
							}
						});
						mApplicationLoaderThread=null;
					}
				}
			};
			mApplicationLoaderThread.start();
		}
	}

	public synchronized void setOnItemSelectedListener(OnItemSelectedListener listener) {
		this.mOnItemSelectedListener=listener;
	}

	public static interface OnItemSelectedListener {
		public void onItemSelected(Rotor rotor, List<ApplicationInfo> appList, 
				ApplicationInfo appInfo, int index);
	}
}
