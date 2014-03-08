package com.example.paint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class PaintCanvas extends View {
	
	private Paint canvasPaint, drawPaint;
	private int paintColor = Color.BLACK, canvasColor = Color.RED;
	private Canvas canvas;
	private Bitmap canvasBitmap;
	private Path drawPath;
	private int currentWidth;
	private int currentHeight;
 
	public PaintCanvas(Context context) {
		this(context, null, 0);
	}

	public PaintCanvas(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PaintCanvas(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// http://code.tutsplus.com/tutorials/android-sdk-create-a-drawing-app-touch-interaction--mobile-19202
		drawPath = new Path();
		drawPaint = new Paint();
		
		drawPaint.setColor(paintColor);
		drawPaint.setStrokeWidth(30); // set initial brush size to 20
		drawPaint.setAntiAlias(true); // makes lines smoother
		drawPaint.setStyle(Paint.Style.STROKE); // make strokes
		drawPaint.setStrokeJoin(Paint.Join.ROUND); // sets curves around bends to round
		drawPaint.setStrokeCap(Paint.Cap.ROUND); // rounds off end of stroke
		
		canvasPaint = new Paint();
		canvasPaint.setColor(canvasColor);
		canvasPaint.setStyle(Paint.Style.FILL);		
		
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888); // initialize bitmap to empty
		canvas = new Canvas(canvasBitmap); //sets the canvas to a bitmap. Use later to pull in images and draw on
		
		// use to center stuff later
		currentWidth = w;
		currentHeight = h;
	}
	
	// Keep as short as possible. Get in, get out
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float xTouch = event.getX();
		float yTouch = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			drawPath.moveTo(xTouch, yTouch); // on press down, draw starting point
			break;
		case MotionEvent.ACTION_MOVE:
			drawPath.lineTo(xTouch, yTouch); // on movement, draw line to where user moves to
			break;
		case MotionEvent.ACTION_UP:
			canvas.drawPath(drawPath, drawPaint); // writes the newly drawn stroke to the canvas
			drawPath.reset(); // reset path for the next stroke
			break;
		default:
			return false;
		}
		
		invalidate(); // update canvas
		return true;
	}

}
