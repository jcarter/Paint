package com.example.paint;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

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
import android.widget.LinearLayout;
import android.widget.TableRow;

public class MainActivity extends Activity implements OnClickListener, OnTouchListener{
	 
	PaintCanvas paintCanvas; 
	ImageButton newPaintingButton, colorSelctorButton; 
	TableRow menuOptions;
	
	// color selector stuff
	LinearLayout colorSelector;
	ColorPicker picker;
	SVBar svBar;
	OpacityBar opacityBar;  
	SaturationBar saturationBar;
	ValueBar valueBar;  

	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setUpColorSelector();
		
		paintCanvas = (PaintCanvas)findViewById(R.id.paint_canvas);
		
		// buttons
		newPaintingButton = (ImageButton)findViewById(R.id.new_canvasIB);
		colorSelctorButton = (ImageButton)findViewById(R.id.color_selectorIB);
		
		menuOptions = (TableRow)findViewById(R.id.menuOptionTR);
		newPaintingButton.setOnClickListener(this);
		colorSelctorButton.setOnClickListener(this);
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
		if (v.getId() == newPaintingButton.getId()) {
			paintCanvas.startNewPainting();
		}
		else if (v.getId() == colorSelctorButton.getId()) {
			showColorSelector();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {	
		if (colorSelector.getVisibility() != View.VISIBLE) {
			paintCanvas.onTouchEvent(event); // handles the ability to draw
			// Menu animation
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				menuOptions.animate().translationY(-100).setDuration(200); // slide menu up
				break;
			case MotionEvent.ACTION_UP:
				menuOptions.animate().translationY(0).setDuration(200); // slide menu down
				break;
			default:
				return false;
			}
		} 
			
		return true;
	}
	
	public void setUpColorSelector() {
		colorSelector = (LinearLayout)findViewById(R.id.colorSelectorLL);
		picker = (ColorPicker) findViewById(R.id.picker);
		svBar = (SVBar) findViewById(R.id.svbar);
		opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
		//saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
		//valueBar = (ValueBar) findViewById(R.id.valuebar);
			
		picker.addSVBar(svBar);
		picker.addOpacityBar(opacityBar);
		//picker.addSaturationBar(saturationBar);
		//picker.addValueBar(valueBar); 
	}
	
	public void showColorSelector() {
		if (colorSelector.getVisibility() == View.VISIBLE) {
			colorSelector.setVisibility(View.GONE);
		} else {
			colorSelector.setVisibility(View.VISIBLE);
		}	
	}

}
