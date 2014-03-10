package com.example.paint;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener{
	
	PaintCanvas paintCanvas; 
	ImageButton newPaintingButton; 
	TableRow menuOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		paintCanvas = (PaintCanvas)findViewById(R.id.paint_canvas);
		newPaintingButton = (ImageButton)findViewById(R.id.imageButton1);
		menuOptions = (TableRow)findViewById(R.id.menuOptionTR);
		newPaintingButton.setOnClickListener(this);
		paintCanvas.setOnTouchListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == newPaintingButton.getId())
			paintCanvas.startNewPainting();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float xTouch = event.getX();
		float yTouch = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			menuOptions.animate().translationY(-100).setDuration(200);
			paintCanvas.getDrawPath().moveTo(xTouch, yTouch); // on press down, go to starting point
			break;
		case MotionEvent.ACTION_MOVE:
			paintCanvas.getDrawPath().lineTo(xTouch, yTouch); // on movement, draw line to where user moves to
			break;
		case MotionEvent.ACTION_UP:
			menuOptions.animate().translationY(0).setDuration(200);
			paintCanvas.getCanvas().drawPath(paintCanvas.getDrawPath(), paintCanvas.getDrawPaint()); // writes the newly drawn stroke to the canvas
			paintCanvas.getDrawPath().reset(); // reset path for the next stroke
			break;
		default:
			return false;
		}
		
		paintCanvas.invalidate();		
		return true;
	}

}
