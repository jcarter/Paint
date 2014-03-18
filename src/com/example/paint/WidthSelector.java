package com.example.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageButton;
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

		// Draws the display for the user to change the brush width
		@Override  
		protected void onDraw(Canvas canvas) {
			canvas.drawLine(50, currentHeight/2, currentWidth-51, currentHeight/2, drawPaint); // draws line so rounded corners can be seen
		}
		
		// Overrides the parent method to make this view not respond to touching
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			return true;
		}
		
		// Draws the line to show user the width they're selecting
		public void reDrawLine(SeekBar bar, int progress, int color) {
			drawPaint.setStrokeWidth(progress);
			drawPaint.setColor(color);
			Log.i("VALUES", "Width: " + drawPaint.getStrokeWidth());
			invalidate();
		}
		
		// Used to fix the issue of having to touch the width bar before the color changed  
		public void setDrawPaint(int color) {
			super.setDrawPaint(color); // calls the parent method to set the color
			invalidate(); // invalidates to immediately change the color
		}
}
