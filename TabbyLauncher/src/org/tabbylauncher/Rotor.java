package org.tabbylauncher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
		private int mCanvasWidth;
		private int mCanvasHeight;
		private Paint mLinePaint;

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

					synchronized (mSurfaceHolder) {
						updatePhysics();
						doDraw(c);
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						mSurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}

		}


		/**
		 * Make the drawing
		 * */
		private void doDraw(Canvas canvas) {

			mLinePaint.setARGB(255, 0, 0, 255);
			canvas.drawCircle(touchDownX, touchDownY, 20, mLinePaint);
			mLinePaint.setARGB(255, 255, 0, 0);
			canvas.drawText("("+lambda+")",20, 20, mLinePaint);
			mLinePaint.setARGB(255, 0, 0, 255);
			canvas.drawLine(touchDownX, touchDownY,mCanvasWidth/2, mCanvasHeight/2, mLinePaint);
			canvas.drawLine(touchDownX, touchDownY,touchDownX, mCanvasHeight/2, mLinePaint);
			canvas.drawLine(touchDownX, mCanvasHeight/2,mCanvasWidth/2, mCanvasHeight/2, mLinePaint);

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

			}

		}



	}

	private Context  mContext 	   ;
	private Vibrator vibrator;
	private org.tabbylauncher.Rotor.AnimationThread thread;
	private float prevX;
	private float prevY;
	private double oldLambda;
	private double lambda;
	private float touchDownX;
	private float touchDownY;
	private long downtime;
	private boolean inversion;
	private double rotation;
	private boolean refresh;


	/*
	 * View Handler 
	 */

	public Rotor(Context context) {
		super(context);
	}


	public Rotor(Context context, AttributeSet attrs) {
		super(context, attrs);


		mContext = context;

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
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		//		if (!hasWindowFocus) thread.pause();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

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
				prevX=event.getX();
				prevY=event.getY();
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
			touchDownX= event.getX();
			touchDownY= event.getY();

			downtime=event.getDownTime();


			double a = angleFromPoint(prevX,prevY,thread.mCanvasWidth,thread.mCanvasHeight);
			double b = angleFromPoint(touchDownX,touchDownY,thread.mCanvasWidth,thread.mCanvasHeight);

			double lambda2 = a-b;

			if((lambda2<0 && lambda > 0 )||(lambda2>0 && lambda < 0 )){
				vibrator.vibrate(202);
				inversion=true;
			}else{
				inversion=false;
			}



			if(lambda2!=0){
				lambda = lambda2;
				refresh=true;

				rotation += lambda2;

				if((rotation % 1)==0 && inversion==false){
					vibrator.vibrate(2);
				}
			}
			prevX=touchDownX;
			prevY=touchDownY;

		}


		return true;
	}    

	private static  double angleFromPoint(float x,float y, int width, int height) {
		double r = Math.atan2(x - width / 2, height / 2 - y);
		return  (int) Math.toDegrees(r);
	}

}
