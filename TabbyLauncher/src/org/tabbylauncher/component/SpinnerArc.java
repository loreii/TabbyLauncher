package org.tabbylauncher.component;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;

public class SpinnerArc {

	private int arc;
	private int x;
	private int y;
	private int rad;
	private Paint mLinePaint;

	public SpinnerArc(int x, int y, int rad) {
		this.x=x;
		this.y=y;
		this.rad=rad;
		
		//init spinner Style 
		mLinePaint = new Paint();
		mLinePaint.setAntiAlias(true);
		mLinePaint.setARGB(155, 55, 55, 55);
		mLinePaint.setTextSize(15f);
		mLinePaint.setStyle(Style.STROKE);
		mLinePaint.setStrokeCap(Paint.Cap.ROUND);
		mLinePaint.setStrokeWidth(25);
	}

	public void draw(Canvas canvas){

		RectF oval = new RectF(x-rad,y- rad, x+rad, y+rad);
		canvas.drawArc(oval, 0, arc , false, mLinePaint);
		arc= (arc+5)%360;
	}

}
