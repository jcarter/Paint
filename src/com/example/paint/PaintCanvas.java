package com.example.paint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


public class PaintCanvas extends View {
	
	private Paint backgroundPaint, drawPaint;
	private int paintColor;

	public PaintCanvas(Context context) {
		this(context, null, 0);
	}

	public PaintCanvas(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PaintCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
		backgroundPaint = new Paint();
		backgroundPaint.setColor(Color.RED);
		backgroundPaint.setStyle(Paint.Style.FILL);
		
		drawPaint = new Paint();
		drawPaint.setColor(Color.BLACK);
		drawPaint.setStyle(Paint.Style.STROKE);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawPaint(backgroundPaint);
	}

}
