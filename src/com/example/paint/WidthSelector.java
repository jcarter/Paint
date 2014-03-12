package com.example.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

	public class WidthSelector extends PaintCanvas {

	public WidthSelector(Context context) {
		super(context);
	}

	public WidthSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WidthSelector(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawLine(0, currentHeight/2, currentWidth-1, currentHeight/2, drawPaint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// makes this view not respond to touching
		return true;
	}
	
	// draws the line to show user the width they're selecting
	public void reDrawLine(SeekBar bar, int progress, int color) {
		drawPaint.setStrokeWidth(bar.getProgress());
		drawPaint.setColor(color);
		Log.i("VALUES", "Width: " + drawPaint.getStrokeWidth());
		invalidate();
	}
	
	// used to fix the issue of having to touch the width bar before the color changed  
	public void setDrawPaint(int color) {
		drawPaint.setColor(color);
		invalidate();
	}

}
