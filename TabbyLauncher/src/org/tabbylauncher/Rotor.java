package org.tabbylauncher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.tabbylauncher.component.SpinnerArc;
import org.tabbylauncher.db.TabbyProvider;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Rotor extends SurfaceView implements SurfaceHolder.Callback  {
	/*
	 * ============ Update animation thread =============
	 */

	class AnimationThread extends Thread {

		private long 		  mTimer;
		private SurfaceHolder mSurfaceHolder;
		private Handler 	  mHandler;
		private Context 	  mContext;

		private boolean mRun=true;		
		private float mCircleRadius;
		private float mCircleExtRadius;
		private float mCircleIntRadius;
		private int mCircleWidth;
		private Paint mLinePaint;
		private int mTouchAlpha;
		private int mCircleRed=0;
		private int mCircleBlue=0;
		private int mCircleGreen=255;
		private int mBitmapSize;
		private Paint mDebugLinePaint;

		//enable debug info
		private boolean debug=true;

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
//							angleIcon(c);
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
				if (interrupted()) {
					mRun=false;
					break;
				}
				try {
					sleep(50);
				} catch (InterruptedException e) {
					mRun=false;
					break;
				}
			}

		}

		private void onPause() {
			mRun=false;
		}

		private void onResume() {
			mRun=true;
			
		}

//		private void angleIcon(Canvas canvas) {
//			ApplicationInfo applicationInfo = mFavorites.get(0);
//			Bitmap bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
//			canvas.drawBitmap(bitmap, 10, 10, null);
//
//			applicationInfo = mFavorites.get(1);
//			bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
//			canvas.drawBitmap(bitmap, mCanvasWidth-bitmap.getWidth(), 10, null);
//
//			applicationInfo = mFavorites.get(2);
//			bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
//			canvas.drawBitmap(bitmap, 10, mCanvasHeight-bitmap.getHeight(), null);
//
//			applicationInfo = mFavorites.get(3);
//			bitmap = ((BitmapDrawable)applicationInfo.icon).getBitmap();
//			canvas.drawBitmap(bitmap, mCanvasWidth-bitmap.getWidth(), mCanvasHeight-bitmap.getHeight(), null);
//		}

		/**
		 * Make the drawing
		 * */
		private void doDraw(Canvas canvas) {
			canvas.save();
			Drawable background = getBackground();
			if (background!=null) {
				background.draw(canvas);
			} else {
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
			}
			if (mTouchAlpha>0) {
				mTouchAlpha-=10;
				if (mTouchAlpha>0)
					mLinePaint.setARGB(mTouchAlpha, 0, 0, 255);
				canvas.drawCircle(touchDownX, touchDownY, 20, mLinePaint);
			}

			if(debug)
				debugGrid(canvas);


			mLinePaint.setStyle(Style.STROKE);
			mLinePaint.setStrokeWidth(mCircleWidth);
			mLinePaint.setStrokeCap(Paint.Cap.BUTT);

			RectF oval = new RectF(mCenterX-mCircleRadius,mCenterY- mCircleRadius, mCenterX+mCircleRadius, mCenterY+mCircleRadius);

			int strokeWidth=40;
			for(Sector sector : round){ 															//render the inner sectors
				mLinePaint.setColor(sector.color);
				mLinePaint.setStrokeWidth(strokeWidth);strokeWidth+=5;
				canvas.drawArc(oval, sector.start, sector.getDegres() , false, mLinePaint);
			}

			mLinePaint.setStyle(Style.FILL);

			mLinePaint.setARGB(255, 255, 255, 255);
			if(debug){
				canvas.drawText("lambda=",mCenterX, mCenterY+10, mLinePaint);
				//				canvas.drawText(applicationList.get(Math.abs(b)%applicationList.size()),mCenterX, mCenterY, mLinePaint);
			}

			synchronized (mApplications) {
				if (mSelectedApp>=0 && applicationReady) {
					ApplicationInfo app = mApplications.get(mSelectedApp);
					Bitmap bitmap = ((BitmapDrawable)app.icon).getBitmap();
					Matrix mtx = new Matrix();

					RectF drawableRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
					RectF viewRect = new RectF(0, 0, mBitmapSize, mBitmapSize);
					mtx.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);

					Bitmap scaledBMP = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
					int bitmapWidth  = (scaledBMP.getWidth()>>1);
					int bitmapHeight = (scaledBMP.getHeight()>>1);

					canvas.drawBitmap(scaledBMP,(mCanvasWidth/2)-bitmapWidth, (mCanvasHeight/2)-bitmapHeight, null);
					scaledBMP.recycle();
					scaledBMP=null;
				}else{

					if(spinnerArc!=null)
						spinnerArc.draw(canvas);
				}
			}

			canvas.restore();
		}


		private SpinnerArc spinnerArc ; 




		private void debugGrid(Canvas canvas) {

			if(canvas==null)
				return;

			// Initialize paints for finger hit
			mDebugLinePaint = new Paint();
			mDebugLinePaint.setAntiAlias(true);

			mDebugLinePaint.setTextSize(15f);

			mDebugLinePaint.setARGB(255, 0, 255, 0);
			canvas.drawText("##("+mSelectedApp+")",touchDownX, touchDownY, mDebugLinePaint);
			mDebugLinePaint.setARGB(255, 255, 0, 0);
			canvas.drawLine(touchDownX, touchDownY,mCanvasWidth/2, mCanvasHeight/2, mDebugLinePaint);
			canvas.drawLine(touchDownX, touchDownY,touchDownX, mCanvasHeight/2, mDebugLinePaint);
			canvas.drawLine(touchDownX, mCanvasHeight/2,mCanvasWidth/2, mCanvasHeight/2, mDebugLinePaint);


			for(int i = 0;i<mCanvasHeight;i+=50){
				canvas.drawText("-> " + i, 0, i,  mDebugLinePaint);
				canvas.drawLine(0, i, mCanvasWidth, i, mDebugLinePaint);
			}

			for(int i = 0;i<mCanvasWidth;i+=50){
				canvas.drawText("-> " + i,i, 20,  mDebugLinePaint);
				canvas.drawLine(i, 0, i, mCanvasHeight, mDebugLinePaint);
			}

		}



		/**
		 * Update physics animation
		 * */
		private void updatePhysics() {

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
				mCircleIntRadius = mCircleExtRadius-mCircleWidth;
				mBitmapSize=((int)mCircleExtRadius)>>1;
	
				spinnerArc = new SpinnerArc(mCenterX,mCenterY,100);
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
	private int lastDownX;
	private int lastDownY;
	private double oldLambda;
	private double lambda;
	private int touchDownX;
	private int touchDownY;
	private long downtime;
	private boolean inversion;
	private double rotation;
	private boolean refresh;
	private Thread mApplicationLoaderThread=null;
	public static ArrayList<ApplicationInfo> mApplications = null;
	private int mSelectedApp=-1;
	private ArrayList<ApplicationInfo> mFavorites;
	private int mCanvasWidth;
	private int mCanvasHeight;
	private int mCenterX;
	private int mCenterY;
	private OnItemSelectedListener mOnItemSelectedListener;
	private OnRotorClickListener mOnRotorClickListener;
	protected boolean applicationReady;

	private static List<Sector> round = new LinkedList<Sector>();

	static{
		round.add(new Sector(Color.BLUE,0,12));
		round.add(new Sector(Color.RED,12,16));
		round.add(new Sector(Color.GREEN,16,32));
		round.add(new Sector(Color.GRAY,32,36));
		round.add(new Sector(Color.YELLOW,36,360));

	}

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
		setZOrderOnTop(true);
		SurfaceHolder holder = getHolder();
		holder.setFormat(PixelFormat.TRANSPARENT);
		holder.addCallback(this);

		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		// create thread only; it's started in surfaceCreated()
		thread = new AnimationThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {}
		});

		setFocusable(true); // make sure we get key events
	}

	public void onPause() {
		thread.onPause();
	}

	public void onResume() {
		thread.onResume();
	}

	TabbyProvider db = new TabbyProvider();
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
//		loadFavorites(mContext);
//		loadApplications(true);
		
		
	}

//	private void loadFavorites(Context context) {
//
//		if(mFavorites==null){
//			mFavorites = new ArrayList<ApplicationInfo>(4);
//		}
//		mFavorites.clear();
//		ApplicationInfo applicationInfo = new ApplicationInfo();
//		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_call);
//		applicationInfo.title = "Phone";
//		applicationInfo.intent = new Intent(Intent.ACTION_DIAL);
//		mFavorites.add(applicationInfo);
//		applicationInfo = new ApplicationInfo();
//		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_send);
//		applicationInfo.title = "Message";
//		applicationInfo.intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
//		mFavorites.add(applicationInfo);
//		applicationInfo = new ApplicationInfo();
//		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_my_calendar);
//		applicationInfo.title = "Calendar";
//		mFavorites.add(applicationInfo);
//		applicationInfo = new ApplicationInfo();
//		applicationInfo.icon = context.getResources().getDrawable(android.R.drawable.ic_menu_agenda);
//		applicationInfo.title = "Agenda";
//		mFavorites.add(applicationInfo);
//	}


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
			int ex=(int)event.getX()-mCenterX;
			int ey=(int)event.getY()-mCenterY;
			int ed=(ex*ex+ey*ey);
			int rq=((int)thread.mCircleIntRadius)*((int)thread.mCircleIntRadius);
			int extrq=((int)thread.mCircleExtRadius)*((int)thread.mCircleExtRadius);
			boolean inInnerCircle = ed<rq;
			boolean outOfCircle = ed>extrq;


			switch (event.getActionMasked()){
			case MotionEvent.ACTION_DOWN:

				Log.d("DEBUG", "get action down");
				prevX=(int)event.getX();
				prevY=(int)event.getY();
				lastDownX=prevX;
				lastDownY=prevY;

				break;
			case MotionEvent.ACTION_UP:
				Log.d("DEBUG", "release action up");
				oldLambda=lambda;
				lambda=0;
				int x=lastDownX-mCenterX;
				int y=lastDownY-mCenterY;
				int rdc=x*x+y*y;
				if (inInnerCircle && (rdc<rq)) {
					if (mOnRotorClickListener!=null && applicationReady) {
						mOnRotorClickListener.onItemClick(this, mApplications, 
								getSelectedAppInfo(), mSelectedApp);
					}
				}
//				else
//					if (outOfCircle) {
//					int idx = x<0?(y<0?0:3):(y<0?1:2);
//					mOnRotorClickListener.onQuadrantListener(idx, mFavorites.get(idx).intent);
//				}

				break;
			default:
				break;
			}


			//update the current touch location
			touchDownX= (int)event.getX();
			touchDownY= (int)event.getY();

			downtime=event.getDownTime();


			if (!inInnerCircle && !outOfCircle) {
				int b = (int) angleFromPoint(touchDownX,touchDownY,mCanvasWidth,mCanvasHeight);
				synchronized (mApplications) {
					int oldSelected = mSelectedApp;
					float appAngle = 360.0f/mApplications.size();
					int c = (int) Math.min((int)b/appAngle,mApplications.size());
					mSelectedApp = c;
					if (mOnItemSelectedListener!=null && oldSelected!=mSelectedApp && applicationReady) {
						mOnItemSelectedListener.onItemSelected(this, mApplications, 
								mApplications.get(mSelectedApp), mSelectedApp);
					}
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
		int d = (int) Math.toDegrees(r);
		return d<0?360+d:d;  
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

//	private void loadApplications(boolean isLaunching) {
//		if (mApplicationLoaderThread==null) {
//			mApplicationLoaderThread = new Thread("ApplicationLoader") {
//
//
//				public void run() {
//					PackageManager manager = getContext().getPackageManager();
//
//					Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
//					mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//					final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
//					Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));
//					ArrayList<ApplicationInfo> appInfos=null;
//					if (apps != null) {
//						final int count = apps.size();
//
//						appInfos = new ArrayList<ApplicationInfo>(count);
//						for (int i = 0; i < count; i++) {
//							ApplicationInfo application = new ApplicationInfo();
//							ResolveInfo info = apps.get(i);
//							application.title = info.loadLabel(manager);
//							application.pakage = info.activityInfo.applicationInfo.packageName;
//							application.setActivity(new ComponentName(
//									info.activityInfo.applicationInfo.packageName,
//									info.activityInfo.name),
//									Intent.FLAG_ACTIVITY_NEW_TASK
//									| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//							application.icon = info.activityInfo.loadIcon(manager);
//							appInfos.add(application);
//						}
//					}
//					synchronized (mApplications) {
//						final boolean changed = appInfos==null||
//								mApplications.size()!=appInfos.size();
//						mApplications.clear();
//						if (appInfos!=null) {
//							int i = 0 ;
//							for (ApplicationInfo info : appInfos) {
//								info.color=ColorUtils.color[i];i=(i+1) % ColorUtils.color.length;
//								mApplications.add(info);
//							}
//						}
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								onApplicationsLoadingFinished(changed);
//							}
//						});
//						mApplicationLoaderThread=null;
//						updateSectors();
//						applicationReady=true;
//					}
//				}
//			};
//			mApplicationLoaderThread.start();
//		}
//	}

	public ApplicationInfo getSelectedAppInfo() {
		synchronized (mApplications) {
			return mApplications.get(mSelectedApp);
		}
	}

	public synchronized void setOnItemSelectedListener(OnItemSelectedListener listener) {
		this.mOnItemSelectedListener=listener;
	}

	public synchronized void setOnItemClickListener(OnRotorClickListener listener) {
		this.mOnRotorClickListener=listener;
	}

	public static interface OnItemSelectedListener {
		public void onItemSelected(Rotor rotor, List<ApplicationInfo> appList, 
				ApplicationInfo appInfo, int index);
	}
	public static interface OnRotorClickListener {
		public void onItemClick(Rotor rotor, List<ApplicationInfo> appList, 
				ApplicationInfo appInfo, int index);
		public void onQuadrantListener(int idx, Intent intent);
	}


	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		if(visibility==0){
			//back on screen
			updateSectors();
		}
		super.onVisibilityChanged(changedView, visibility);
	}
	
	
	public void updateSectors(){
		if (mApplications.isEmpty())
			return;
		
		ArrayList<ApplicationInfo> tmp = new ArrayList<ApplicationInfo>();
		//remove all unset colors
		for (ApplicationInfo i : mApplications){
			if(i.color!=-1)
				tmp.add(i);
		}
		//sort refined list by color
		Comparator<? super ApplicationInfo> comparator= new Comparator<ApplicationInfo>() {
			@Override
			public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
				return lhs.color.compareTo(rhs.color);
			}
		};
		Collections.sort(tmp, comparator);

		round.clear();

		int appAngle = 360 / mApplications.size();


		Sector s = new Sector(tmp.remove(0).color, 0, appAngle);
		for(ApplicationInfo i:tmp){
			if(!i.color.equals(s.color)){
				round.add(s);

				s= new Sector(i.color, s.end, s.end);

			}
			s.end+=appAngle;

		}

	}



	public synchronized void setArrayElement(Thread applicationLoaderThread, 
			ArrayList<ApplicationInfo> applications){
		mApplicationLoaderThread=applicationLoaderThread;
		mApplications=applications;
	}
}


class Sector{
	int color;
	private int degres;
	int start;
	int end;

	public Sector(int color, int start,int end) {
		super();
		this.color = color;
		this.start=start;
		this.end=end;

	}

	public int getDegres() {
		return this.degres = end-start;
	}

}